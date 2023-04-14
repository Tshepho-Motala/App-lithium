package lithium.service.accounting.provider.internal.services;

import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SummaryAccountService {

	@Autowired PeriodService periodService;
	@Autowired SummaryAccountRepository repository;
	@Autowired LithiumMetricsService metrics;

	@Transactional(rollbackFor=java.lang.Exception.class, propagation=Propagation.MANDATORY)
	@TimeThisMethod
	public void adjust(TransactionEntry e) {
		Long debitCents = (e.getAmountCents() > 0) ? e.getAmountCents(): 0;
		Long creditCents = (e.getAmountCents() > 0) ? 0: e.getAmountCents() * -1;

		DateTime now = DateTime.now();
		DateTime tranDate = new DateTime(e.getDate().getTime());

		for (int granularity = 1; granularity <= 5; granularity++) {
			SW.start("findOrCreatePeriod " + granularity);
			Period period = periodService.findOrCreatePeriod(tranDate, e.getAccount().getDomain(), granularity);
			SW.stop();

			SummaryAccount account = findOrCreate(period, e.getAccount(), true);
			account.setDebitCents(account.getDebitCents() + debitCents);
			account.setCreditCents(account.getCreditCents() + creditCents);
			account.setClosingBalanceCents(account.getClosingBalanceCents() + debitCents - creditCents);
			account.setTranCount(account.getTranCount() + 1);
			repository.save(account);
					
			if (period.getGranularity() != Period.GRANULARITY_TOTAL) {
				/* Ok, so the current period's statistics are stored. If however this is not the current period, we have to update future periods. */
				if (tranDate.withTimeAtStartOfDay().isBefore(now.withTimeAtStartOfDay())) {
					SW.start("future.adjust");
					repository.adjustBalances(e.getAmountCents(), e.getAccount(), granularity, e.getDate());
					SW.stop();
				}
			}

		}
	}

	public SummaryAccount findOrCreate(Period period, Account account, boolean logErrorOnCreate) {
		
		long openingBalance = 0L;
		SummaryAccount prev = null;
		
		SummaryAccount current = repository.findByPeriodAndAccount(period, account);
		if (current != null) return current;

		if (period.getGranularity() != Period.GRANULARITY_TOTAL) {
			SW.start("findpreviousPeriod");
			/* This will optimistically lock the previous period if it exists. We need to ensure that no one is busy updating it, so we
			 * save it after we have saved our new period just so that we can confirm its version has not changed.
			 */
			prev = repository.findFirstByAccountAndPeriodGranularityAndPeriodDateStartBeforeOrderByPeriodDateStartDesc(
				account, period.getGranularity(), period.getDateStart()
			);
			SW.stop();
			if (prev != null) openingBalance = prev.getClosingBalanceCents();
		}

		SummaryAccount newsa = SummaryAccount.builder()
				.account(account).period(period)
				.openingBalanceCents(openingBalance).closingBalanceCents(openingBalance)
				.debitCents(0L).creditCents(0L)
				.damaged(true)
				.tranCount(0L)
				.build();

		if (logErrorOnCreate) log.error("No summary account yet, creating one " + newsa);
		newsa = repository.save(newsa);
		log.info("Summary account created " + newsa);
		
		return newsa;

	}

}
