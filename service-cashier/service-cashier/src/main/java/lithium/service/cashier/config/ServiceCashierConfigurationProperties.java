package lithium.service.cashier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "lithium.services.cashier")
public class ServiceCashierConfigurationProperties {
	private Boolean copyDomainLimits = false;
	private Long cleanupSchedulingInMilliseconds;
	private Long finishPendingCancelDelayMs;
	private Long finishPendingCancelIntervalMs;
	private Long transactionDefaultTtl;
	private Long transactionExtraFieldsInMilliseconds;
	
	private TransactionGeoDeviceLabels transactionGeoDeviceLabels = new TransactionGeoDeviceLabels();
	
	@Data
	public static class TransactionGeoDeviceLabels {
		private boolean enabled;
	}
}
