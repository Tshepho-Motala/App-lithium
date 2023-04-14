package lithium.service.casino.provider.sportsbook.stream;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.provider.sportsbook.api.controllers.SettleController;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.util.ExceptionMessageUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@ConditionalOnProperty(
    name = "lithium.services.casino.provider.sportsbook.performance-testing.settle-multi.queue-into-batch",
    havingValue = "false")
@EnableBinding(PerformanceTestSettlementQueueSink.class)
public class PerformanceTestSettlementQueueProcessor {

  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private SettleController settleController;

  @StreamListener(PerformanceTestSettlementQueueSink.INPUT)
  public void process(SettleMultiRequest multiRequest) {
    try {
      log.debug("PerformanceTestSettlementQueueProcessor received SettleMultiRequest" + multiRequest);
      settleController.settleMulti("en_US", multiRequest);
    } catch (ErrorCodeException ec) {
      log.warn("PerformanceTestSettlementQueueProcessor " + ec + " " + multiRequest);
      // Because we can; this is only a test.
      return;
    } catch (Exception e) {
      log.error("PerformanceTestSettlementQueueProcessor " + ExceptionMessageUtil.allMessages(e) + " " + multiRequest, e);
      // Because we can; this is only a test.
      return;
    }
  }

}