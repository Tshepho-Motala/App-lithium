package lithium.service.cashier.processor.cheque;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.cheque.DoProcessorChequeAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.settlement.client.SettlementClient;
import lithium.service.settlement.client.objects.Address;
import lithium.service.settlement.client.objects.BankDetails;
import lithium.service.settlement.client.objects.BatchSettlements;
import lithium.service.settlement.client.objects.Domain;
import lithium.service.settlement.client.objects.Settlement;
import lithium.service.settlement.client.objects.SettlementEntry;
import lithium.service.settlement.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorChequeAdapter {
	@Autowired LithiumServiceClientFactory services;
	@Autowired ModelMapper modelMapper;
	
	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		try {
			SettlementClient client = services.target(SettlementClient.class, "service-settlement", true);
			Date dateStart = new DateTime(new Date()).withTimeAtStartOfDay().toDate();
			Date dateEnd = new DateTime(new Date()).plusDays(1).withTime(23, 59, 59, 0).toDate();
			
			String baseBatchFormat = request.getProperties().get("baseBatchFormat");
			if (baseBatchFormat == null || baseBatchFormat.isEmpty())
				baseBatchFormat = "cashier-withdrawals-";
			SimpleDateFormat batchNameDf = new SimpleDateFormat("dd.MM.yyyy");
			String batchName = baseBatchFormat + batchNameDf.format(new Date());
			
			SimpleDateFormat settlementDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Settlement settlement = client.findByUser(batchName,
				URLEncoder.encode(request.getUser().getDomain() + "/" + request.getUser().getUsername(), "UTF-8"),
				settlementDf.format(dateStart), settlementDf.format(dateEnd)).getData();
			if (settlement == null) {
				Address physicalAddress = null;
				if (request.getUser().getResidentialAddress() != null) {
					physicalAddress = modelMapper.map(request.getUser().getResidentialAddress(), Address.class);
				}
				
				Address billingAddress = null;
				if (request.getUser().getPostalAddress() != null) {
					billingAddress = modelMapper.map(request.getUser().getPostalAddress(), Address.class);
				}
				
				BankDetails bankDetails = BankDetails.builder()
				.accountNumber(request.stageInputData(1, "accountNumber"))
				.build();
				
				settlement = Settlement.builder()
				.domain(Domain.builder().name(request.getUser().getDomain()).build())
				.user(User.builder().guid(request.getUser().getDomain() + "/" + request.getUser().getUsername()).build())
				.createdBy(request.getUser().getDomain() + "/" + request.getUser().getUsername())
				.dateStart(dateStart)
				.dateEnd(dateEnd)
				.physicalAddress(physicalAddress)
				.billingAddress(billingAddress)
				.bankDetails(bankDetails)
				.batchSettlements(BatchSettlements.builder().name(batchName).build())
				.build();
				settlement = client.create(settlement).getData();
			}
			
			Map<String, String> labelValueMap = new LinkedHashMap<String, String>();
			labelValueMap.put("transactionId", String.valueOf(request.getTransactionId()));

			// Original amount for withdrawal request
			SettlementEntry entry = SettlementEntry.builder()
			.settlement(settlement)
			.amount(request.processorCommunicationAmount())
			.dateStart(dateStart)
			.dateEnd(dateEnd)
			.description("Withdrawal (" + request.getTransactionId() + ") for " + request.processorCommunicationAmount().setScale(2, RoundingMode.CEILING))
			.labelValueMap(labelValueMap)
			.build();
			settlement = client.addSettlementEntry(settlement.getId(), entry).getData();

			//Fees deducted from orignial amount if present
			String feeAmountString = null;
			//String playerAmountString = null;
			try {
				feeAmountString = request.stageOutputData(1, "accountingFee");
				//playerAmountString = request.stageOutputData(1, "playerAmount");
			} catch (Exception e) {
				//This could happen if no fees are added to the transaction
			}
				if (feeAmountString != null) {
					BigDecimal feeAmount = new BigDecimal(feeAmountString); //Received in decimal value, not cents
					SettlementEntry feeEntry = SettlementEntry.builder()
							.settlement(settlement)
							.amount(feeAmount.negate())
							.dateStart(dateStart)
							.dateEnd(dateEnd)
							.description("Withdrawal fee for (" + request.getTransactionId() + ")")
							.labelValueMap(labelValueMap)
							.build();
				settlement = client.addSettlementEntry(settlement.getId(), feeEntry).getData();
			}
			response.setOutputData(1, "account_info", request.stageInputData(1, "accountNumber"));

			return DoProcessorResponseStatus.SUCCESS;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}
}
