package lithium.service.casino.provider.sportsbook.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PerformanceTestSettlementQueueSink {

  String INPUT = "performance-test-settlement-queue-input";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}