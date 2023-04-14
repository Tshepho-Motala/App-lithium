package lithium.service.casino.provider.sportsbook.api.controllers;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SportsbookMetricService;
import lithium.metrics.builders.sportsbook.EntryPoint;
import lithium.metrics.builders.sportsbook.SportsbookBetStatus;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status472NotAllowedToTransactException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiResponse;
import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lithium.service.casino.provider.sportsbook.services.SettleService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@Slf4j
public class SettleController {
    @Autowired @Setter
    SettleService service;

    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    SportsbookMetricService metrics;

    @PostMapping("/settle")
    public SettleMultiResponse settleMulti(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody SettleMultiRequest multiRequest
    ) throws
            Status401UnAuthorisedException, Status405UserDisabledException, Status422DataValidationError,
            Status444ReferencedEntityNotFound, Status470HashInvalidException,
            Status471InsufficientFundsException, Status472NotAllowedToTransactException,
            Status473DomainBettingDisabledException, Status474DomainProviderDisabledException,
            Status500InternalServerErrorException, Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException, Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException, Status493MonthlyLossLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status496PlayerCoolingOffException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status492DailyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status484WeeklyLossLimitReachedException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        SettleMultiContext context = SettleMultiContext.builder()
                .request(multiRequest)
                .response(new SettleMultiResponse())
                .locale(new Locale(locale))
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(multiRequest.getGuid()))
                .build();

        metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_SYNC,
            context.sportsbookBetType(), SportsbookBetStatus.START);

        try {
            log.info("settlemulti pre " + context);
            try {
                /**
                 * If the multi-settlemnent request passes the validation checks, then the
                 * settlement will be stored in the settlement DB and then offloaded to a priority
                 * multi-settlement adjustment queue to process the bulk settlement updates async.
                 */
                service.validateAndCreateSettle(context);
                service.triggerAsyncSettlement(context);
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("settlemulti duplicate " + de + " " + context);
            }
            service.updateResponse(context);

            metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_SYNC,
                context.sportsbookBetType(), SportsbookBetStatus.SUCCESS);

            long diffInMilliseconds =
                new Date().getTime() - context.getSettlement().getCreatedDate();

            metrics.recordTime(EntryPoint.SETTLE_MULTI_SYNC.name(),
                "Settlement response time", diffInMilliseconds, TimeUnit.MILLISECONDS,
                "domainName", context.domainName(),
                "type", context.sportsbookBetType().name().toLowerCase());

            log.info("settlemulti post | createdDate=" +
                context.getSettlement().getCreatedDate() + " | latency=" + diffInMilliseconds +
                " | " + context);

            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("settlemulti " + ec + " " + context);
            metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_SYNC,
                context.sportsbookBetType(), SportsbookBetStatus.FAILED, "errorCode",
                ec.getErrorCode());
            throw ec;
        } catch (Exception e) {
            log.error("settlemulti " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            metrics.counter(context.domainName(), EntryPoint.SETTLE_MULTI_SYNC,
                context.sportsbookBetType(), SportsbookBetStatus.FAILED, "errorCode", "500");
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
