package lithium.service.casino.provider.sportsbook.storage.objects;

import lithium.service.casino.provider.sportsbook.config.Properties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueRateLimitProperties {
    private boolean enabled = false;
    private int minDelayMs = 50;
    private int maxDelayMs = 100;

    public static QueueRateLimitProperties buildResettleProperties(Properties properties) {
        return QueueRateLimitProperties.builder()
            .enabled(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitResettle()
                .isEnabled())
            .maxDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitResettle().getMaxDelayMs())
            .minDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitResettle().getMinDelayMs())
            .build();
    }

    public static QueueRateLimitProperties buildWinProperties(Properties properties) {
        return QueueRateLimitProperties.builder()
            .enabled(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitWin().isEnabled())
            .maxDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitWin().getMaxDelayMs())
            .minDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitWin().getMinDelayMs())
            .build();
    }

    public static QueueRateLimitProperties buildLossProperties(Properties properties) {
        return QueueRateLimitProperties.builder()
            .enabled(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitLoss()
                .isEnabled())
            .maxDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitLoss().getMaxDelayMs())
            .minDelayMs(properties.getQueueRateLimiter()
                .getSettlementQueue().getLimitLoss().getMinDelayMs())
            .build();
    }
}