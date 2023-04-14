package lithium.service.casino.provider.sportsbook.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.services.casino.provider.sportsbook")
@Configuration
public class Properties {
	private QueueRateLimiter queueRateLimiter = new QueueRateLimiter();

	@Data
	public class QueueRateLimiter {
		private SettlementQueue settlementQueue = new SettlementQueue();
	}

	@Data
	public class SettlementQueue {
		private LimitWin limitWin = new LimitWin();
		private LimitResettle limitResettle = new LimitResettle();
		private LimitLoss limitLoss = new LimitLoss();
	}

	@Data
	public class LimitWin {
		private boolean enabled = false;
		private int minDelayMs = 50;
		private int maxDelayMs = 100;
	}

	@Data
	public class LimitResettle {
		private boolean enabled = false;
		private int minDelayMs = 50;
		private int maxDelayMs = 100;
	}

	@Data
	public class LimitLoss {
		private boolean enabled = true;
		private int minDelayMs = 50;
		private int maxDelayMs = 100;
	}
}
