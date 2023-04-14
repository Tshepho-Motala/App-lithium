package lithium.service.casino.provider.sportsbook.stream;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@ComponentScan(basePackages={"lithium.service.casino.provider.sportsbook"})
public class SettlementBalanceAdjustTriggerConfiguration {

  public static final String EXCHANGE = "sportsbook-settlement-queue";
  public static final String ROUTING_KEY = "sportsbook-settlement-queue.sportsbook-settlement-group";

  /**
   * A larger number = higher priority
   * Win transactions to take priority over loss transactions
   */
  public static final Integer WIN_PRIORITY_NUMBER = 3;
  public static final Integer RESETTLE_PRIORITY_NUMBER = 2;
  public static final Integer LOSS_PRIORITY_NUMBER = 1;

  @Value("${lithium.services.casino.provider.sportsbook.sportsbook-settlement-queue.consumers:1}")
  public int consumers;

  @Bean
  public Queue settlementBalanceAdjustTriggerQueue() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-max-priority", 3);
    return new Queue(ROUTING_KEY, true, false, false, args);
  }

  @Bean
  public CustomExchange settlementBalanceAdjustTriggerExchange() {
    Map<String, Object> args = new LinkedHashMap<>();
    return new CustomExchange(EXCHANGE, "direct", true, false, args);
  }

  @Bean
  public Binding promotionRestrictionTriggerBinding(Queue settlementBalanceAdjustTriggerQueue,
      Exchange settlementBalanceAdjustTriggerExchange) {
    return BindingBuilder
        .bind(settlementBalanceAdjustTriggerQueue)
        .to(settlementBalanceAdjustTriggerExchange)
        .with(ROUTING_KEY)
        .noargs();
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory settlementBalanceAdjustListenerContainerFactory(
      ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor,
      Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jackson2JsonMessageConverter);
    factory.setConcurrentConsumers(consumers);
    return factory;
  }
}
