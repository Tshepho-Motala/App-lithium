package lithium.service.casino.provider.sportsbook.services;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lithium.metrics.SportsbookMetricService;
import lithium.metrics.builders.sportsbook.SportsbookBetType;
import lithium.service.casino.provider.sportsbook.config.Properties;
import lithium.service.casino.provider.sportsbook.storage.objects.QueueRateLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueueRateLimiter {
    @Autowired
    private Properties properties;

    @Autowired
    SportsbookMetricService metric;

    public void limitQueueRate(String queueName, SportsbookBetType betType) {
        QueueRateLimitProperties rateLimitProperties = limitBetType(betType);
        int delay = 0;

        if (rateLimitProperties.isEnabled()) {
            delay = new Random().nextInt(rateLimitProperties.getMinDelayMs(), rateLimitProperties.getMaxDelayMs());
        }

        log.trace("QueueRateLimiter.limitQueueRate | queue: {}, enabled: {}, minDelayMs: {}," +
                " maxDelayMs: {}, delay: {}", queueName, rateLimitProperties.isEnabled(),
            rateLimitProperties.getMinDelayMs(), rateLimitProperties.getMaxDelayMs(), delay);

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error("QueueRateLimiter.limitQueueRate | Unable to implement delay | {}",
                        e.getMessage(), e);
            }
        }
        metric.recordTime(queueName + "_latency", "QueueRateLimiter.limitQueueRate.delay",
            delay, TimeUnit.MILLISECONDS, "type", betType.name().toLowerCase(Locale.ROOT));
    }

    private QueueRateLimitProperties limitBetType(SportsbookBetType betType) {
        QueueRateLimitProperties queueRateLimitProperties = new QueueRateLimitProperties();
        switch (betType) {
            case LOSS -> {
                queueRateLimitProperties = QueueRateLimitProperties.buildLossProperties(properties);
            }
            case WIN -> {
                queueRateLimitProperties = QueueRateLimitProperties.buildWinProperties(properties);
            }
            case RESETTLE -> {
                queueRateLimitProperties = QueueRateLimitProperties.buildResettleProperties(properties);
            }
        }
        log.trace("QueueRateLimiter.limitBetType: {} properties: {}", betType.name(), queueRateLimitProperties);
        return queueRateLimitProperties;
    }
}
