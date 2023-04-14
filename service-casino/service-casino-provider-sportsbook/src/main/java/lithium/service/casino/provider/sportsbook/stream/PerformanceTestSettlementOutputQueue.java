package lithium.service.casino.provider.sportsbook.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PerformanceTestSettlementOutputQueue {

  String OUTPUT = "performance-test-settlement-queue-output";

  @Output(OUTPUT)
  public MessageChannel outputQueue();
}