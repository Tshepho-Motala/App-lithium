package lithium.service.casino.provider.sportsbook.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"lithium.service.casino.provider.sportsbook"})
@EnableBinding({PerformanceTestSettlementOutputQueue.class})
public class PerformanceTestSettlementQueueConfiguration {

}