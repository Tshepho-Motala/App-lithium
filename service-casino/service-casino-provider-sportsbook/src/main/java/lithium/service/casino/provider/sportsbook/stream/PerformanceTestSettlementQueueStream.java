package lithium.service.casino.provider.sportsbook.stream;

import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PerformanceTestSettlementQueueStream {

  @Autowired
  PerformanceTestSettlementOutputQueue performanceTestSettlementOutputQueue;

  public void triggerPerformanceTestSettlement(SettleMultiRequest request) {
    performanceTestSettlementOutputQueue.outputQueue().send(
        MessageBuilder.<SettleMultiRequest>withPayload(request).build());
  }
}
