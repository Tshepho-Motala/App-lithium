package lithium.service.accounting.domain.summary.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.shards.objects.Shard;
import lithium.shards.ShardsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SummaryService {
	@Autowired private SummaryService self;
	@Autowired private DomainService domainService;
	@Autowired private SharedDataService sharedDataService;
	@Autowired private ShardsRegistry shardsRegistry;
	@Autowired private SummaryDomainService summaryDomainService;
	@Autowired private SummaryDomainLabelValueService summaryDomainLabelValueService;
	@Autowired private SummaryDomainTransactionTypeService summaryDomainTransactionTypeService;

	private static final String SHARD_POOL = "AdjustmentQueueProcessor";

	// Persist shared data before the start of the transaction
	@TimeThisMethod
	public void process(String threadName, List<CompleteTransaction> transactions)
			throws Status500InternalServerErrorException {
		SW.start("domain");
		Domain domain = domainService.findOrCreate(transactions.get(0)
				.getTransactionEntryList().get(0)
				.getAccount()
				.getDomain()
				.getName());
		SW.stop();
		sharedDataService.findOrCreates(domain, transactions);
		Shard shard = shardsRegistry.get(SHARD_POOL, threadName);
		self.process(shard, threadName, domain, transactions);
	}

	@TimeThisMethod
	@Transactional(rollbackFor = Exception.class)
	public void process(Shard shard, String threadName, Domain domain, List<CompleteTransaction> transactions)
			throws Status500InternalServerErrorException {
		log.trace("{} - Processing domain summaries for transactions | {}", threadName, transactions);

		try {
			SW.start("summaryDomainService.process");
			summaryDomainService.process(shard.getUuid(), domain, transactions);
			SW.stop();

			SW.start("summaryDomainLabelValueService.process");
			summaryDomainLabelValueService.process(shard.getUuid(), domain, transactions);
			SW.stop();

			SW.start("summaryDomainTransactionTypeService.process");
			summaryDomainTransactionTypeService.process(shard.getUuid(), domain, transactions);
			SW.stop();
		} catch (Exception e) {
			log.error("Failed to process adjustment | Transactions: {} | {}", transactions, e.getMessage(), e);
			throw new Status500InternalServerErrorException(e.getMessage());
		}
	}
}
