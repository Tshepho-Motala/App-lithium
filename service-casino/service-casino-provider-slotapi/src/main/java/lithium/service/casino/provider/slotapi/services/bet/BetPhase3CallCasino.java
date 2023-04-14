package lithium.service.casino.provider.slotapi.services.bet;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequestKindEnum;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.context.BetContext;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.math.CurrencyAmount;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class BetPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    CachingDomainClientService cachingDomainClientService;

    public void callCasinos(
        BetContext context
    ) throws
        Status405UserDisabledException,
        Status423InvalidBonusTokenException,
        Status424InvalidBonusTokenStateException,
        Status438PlayTimeLimitReachedException,
        Status471InsufficientFundsException,
        Status473DomainBettingDisabledException,
        Status474DomainProviderDisabledException,
        Status478TimeSlotLimitException,
        Status484WeeklyLossLimitReachedException,
        Status485WeeklyWinLimitReachedException,
        Status490SoftSelfExclusionException,
        Status491PermanentSelfExclusionException,
        Status492DailyLossLimitReachedException,
        Status493MonthlyLossLimitReachedException,
        Status494DailyWinLimitReachedException,
        Status495MonthlyWinLimitReachedException,
        Status496PlayerCoolingOffException,
        Status500UnhandledCasinoClientException
    {
        double balance = 0.0;

        SW.start("bet.casino.bet." + context.getBet().getBetTransactionId());

        try {
            CasinoTranType tranType = CasinoTranType.CASINO_BET;

            if (context.getRequest().getKind() == BetRequestKindEnum.FREE_BET) {
                tranType = CasinoTranType.CASINO_BET_FREESPIN;
            }

            EBalanceAdjustmentComponentType adjustmentComponentType = (context.getRequest().getKind() == BetRequestKindEnum.FREE_BET)
                    ? EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET : EBalanceAdjustmentComponentType.CASINO_BET;

            BalanceAdjustmentRequest casinoRequest = BalanceAdjustmentRequest.builder()
                    .roundId(context.getRequest().getRoundId())
                    .transactionId(context.getBet().getBetRound().getGuid() + ":" + context.getBet().getBetTransactionId())
                    .externalTimestamp(context.getRequest().getTransactionTimestamp())
                    .gameGuid(context.getDomainName() + "/" + moduleInfo.getModuleName() + "_" + context.getBet().getBetRound().getGame().getGuid())
                    .sessionId(context.getBet().getBetRound().getSessionId())
                    .roundFinished(context.getBet().getBetRound().isComplete())
                    .bonusTran(false)
                    .bonusId(-1)
                    .currencyCode(context.getRequest().getCurrencyCode())
                    .userGuid(context.getBet().getBetRound().getUser().getGuid())
                    .transactionTiebackId(null)
                    .realMoneyOnly(true)
                    .gameSessionId(context.getBet().getBetRound().getGuid())
                    .performAccessChecks(true)
                    .persistRound(true)
                    .shouldPerformConstraintValidations(true)
                    .build();

            casinoRequest.setDomainName(context.getDomainName());
            casinoRequest.setProviderGuid(context.getDomainName() + "/" + moduleInfo.getModuleName());

            ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();

            BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
                    .betTransactionId(context.getRequest().getBetTransactionId())
                    .amount(CurrencyAmount.fromAmount(context.getBet().getAmount()).toCents())
                    .adjustmentType(adjustmentComponentType)
                    .transactionIdLabelOverride(context.getRequest().getBetTransactionId())
                    .additionalReference(null)
                    .build();

            adjustmentComponents.add(bac);
            casinoRequest.setAdjustmentComponentList(adjustmentComponents);
            String locale = cachingDomainClientService.domainLocale(context.getDomainName());
            BalanceAdjustmentResponse response = casinoService.multiBetV1(casinoRequest, locale);

            log.debug("BetResponse from casino " + response);

            switch (response.getResult()) {
                case TRANSACTION_DATA_VALIDATION_ERROR:
                case NEGATIVE_BALANCE_ERROR:
                case INTERNAL_ERROR: {
                    throw new Status500UnhandledCasinoClientException("Internal error received from Casino");
                }
                case INSUFFICIENT_FUNDS: {
                    throw new Status471InsufficientFundsException();
                }
                case SUCCESS: {
                    balance = CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue();

                    // TODO Why is this a string?
                    String extSystemTransactionIdString = response.getAdjustmentResponseComponentList().stream().findFirst().orElse(null).getExtSystemTransactionId();
                    Long extSystemTransactionId = Long.parseLong(extSystemTransactionIdString);
                    context.getBet().setLithiumAccountingId(extSystemTransactionId);
                    context.getBet().setBalanceAfter(balance);
                }
            }

        } catch (ErrorCodeException ece) {
            throw ece;
        } finally {
            SW.stop();
            SW.start("bet.casino.storeresult." + context.getBet().getBetTransactionId());
            betRepository.save(context.getBet());
            SW.stop();
        }
    }
}
