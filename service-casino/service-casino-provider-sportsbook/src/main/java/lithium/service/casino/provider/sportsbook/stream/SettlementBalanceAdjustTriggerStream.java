package lithium.service.casino.provider.sportsbook.stream;

import static lithium.service.casino.provider.sportsbook.stream.SettlementBalanceAdjustTriggerConfiguration.*;

import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementBalanceAdjustTriggerStream {

  private final RabbitTemplate rabbitTemplate;

  // A settlement request may have multiple bets send in a single request. If any of these contain
  // wins or resettlements, then we will prioritise it.
  public void trigger(SettleMultiContext context) {

    //To keep things simple, if any of the bets is a win, it will be prioritised.
    if (context.hasWin()) {
      sendMessage(context, WIN_PRIORITY_NUMBER);
    } else if (context.hasResettlement()) {
      sendMessage(context, RESETTLE_PRIORITY_NUMBER);
    } else {
      sendMessage(context, LOSS_PRIORITY_NUMBER);
    }
  }

  private void sendMessage(SettleMultiContext context, Integer priority) {
    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY,
        context, m -> {
          m.getMessageProperties().setPriority(priority);
          return m;
        });
  }
}
