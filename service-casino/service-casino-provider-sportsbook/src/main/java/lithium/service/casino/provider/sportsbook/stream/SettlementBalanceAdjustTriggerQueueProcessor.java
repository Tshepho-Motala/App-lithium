package lithium.service.casino.provider.sportsbook.stream;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import lithium.exceptions.ErrorCodeException;
import lithium.metrics.SportsbookMetricService;
import lithium.metrics.builders.sportsbook.EntryPoint;
import lithium.metrics.builders.sportsbook.SportsbookBetStatus;
import lithium.metrics.builders.sportsbook.SportsbookBetType;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lithium.service.casino.provider.sportsbook.services.QueueRateLimiter;
import lithium.service.casino.provider.sportsbook.services.SettleService;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SettlementBalanceAdjustTriggerQueueProcessor {

  CachingDomainClientService cachingDomainClientService;
  SettleService settleService;
  SportsbookMetricService metrics;
  QueueRateLimiter queueRateLimiter;

  @Autowired
  public SettlementBalanceAdjustTriggerQueueProcessor(
      CachingDomainClientService cachingDomainClientService, SettleService settleService,
      SportsbookMetricService metrics, QueueRateLimiter queueRateLimiter) {
    this.cachingDomainClientService = cachingDomainClientService;
    this.settleService = settleService;
    this.metrics = metrics;
    this.queueRateLimiter = queueRateLimiter;
  }


  @RabbitListener(queues = SettlementBalanceAdjustTriggerConfiguration.ROUTING_KEY)
  public void process(SettleMultiContext context) {

    try {
      log.info("settlemulti async pre | priority=" + context.sportsbookBetType().name() +
          " | " + context);

      // The idea is to limit loss bets; but have wins go through without any artificial delays
      queueRateLimiter.limitQueueRate(SettlementBalanceAdjustTriggerConfiguration.ROUTING_KEY,
          context.sportsbookBetType());

      settleService.processSettleMultiAdjustments(context);

      metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_ASYNC,
          context.sportsbookBetType(), SportsbookBetStatus.SUCCESS);

      long diffInMilliseconds = new Date().getTime() - context.getSettlement().getCreatedDate();

      metrics.recordTime(EntryPoint.SETTLE_MULTI_ASYNC.name() + "_latency", "Time taken to complete a "
              + "settlement", diffInMilliseconds, TimeUnit.MILLISECONDS,
          "domainName", context.domainName(),
          "type", context.sportsbookBetType().name().toLowerCase());

      log.info("settlemulti async post | completedDate=" + context.getSettlement()
          .getModifiedDate() +
          " | latency=" + diffInMilliseconds + " | " + context);

      return;
    } catch (Status422DataValidationError validationError) {
      if (!suppressValidationError(context)) {
        log.error(
            "A settlement has been found in the queue for an unknown settlement, retrying until"
                + " someone sorts this out - suppress me with domain_setting.suppress_sportsbook_data_validation_error=true "
                + "| SettleMultiContext = " + context, validationError);
      }

      metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_ASYNC,
          context.sportsbookBetType(), SportsbookBetStatus.FAILED,
          "errorCode", String.valueOf(validationError.getCode()));

      throw validationError;
    } catch (Status409DuplicateSubmissionException duplicateSubmissionException) {
      // A Settlement has been found on the queue which has already been processed
      log.info(duplicateSubmissionException.getMessage() + " | " + context);

      return;
    } catch (ErrorCodeException errorCodeException) {
      log.error("Something terrible has happened and settlements are now being requeued "
          + "| " + context, errorCodeException);

      metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_ASYNC,
          context.sportsbookBetType(), SportsbookBetStatus.FAILED,
          "errorCode", String.valueOf(errorCodeException.getCode()));

      throw errorCodeException;
    } catch (Exception e) {
      log.error("Something terrible has happened and settlements are now being requeued "
          + "| " + context, e);
      metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_ASYNC,
          context.sportsbookBetType(), SportsbookBetStatus.FAILED, "errorCode" , "500");

      throw e;
    }
  }

  private boolean suppressValidationError(SettleMultiContext context) {
    String setting = cachingDomainClientService
        .retrieveDomainFromDomainService(context.domainName())
        .findDomainSettingByName("suppress_sportsbook_data_validation_error")
        .orElse(null);
    return Boolean.parseBoolean(setting != null ? setting : "false");
  }

  private static SportsbookBetType priorityTaken(SettleMultiContext context) {
    if (context.hasWin()) {
      return SportsbookBetType.WIN;
    }
    if (context.hasResettlement()) {
      return SportsbookBetType.RESETTLE;
    }
    return SportsbookBetType.LOSS;
  }
}
