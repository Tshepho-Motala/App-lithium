package lithium.service.casino.provider.slotapi.services.betresult;

import lithium.exceptions.ErrorCodeException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.config.ProviderConfigService;
import lithium.service.casino.provider.slotapi.context.BetResultContext;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultRepository;
import lithium.math.CurrencyAmount;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class BetResultPhase3CallCasino {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetResultRepository betResultRepository;

    @Autowired @Setter
    CachingDomainClientService cachingDomainClientService;

    public void callCasino(
        BetResultContext context,
        BetResultRequest request
    ) throws
        Status422DataValidationError,
        Status500UnhandledCasinoClientException
    {
        SW.start("betresult.casino.handle." + context.getBetResult().getBetResultTransactionId());

        try {
            long returns = CurrencyAmount.fromAmount(context.getBetResult().getReturns()).toCents();

            EBalanceAdjustmentComponentType adjustmentComponentType;

            switch (request.getKind()) {
                case WIN: adjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_WIN; break;
                case LOSS: adjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_LOSS; break;
                case VOID: adjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_VOID; break;
                case FREE_WIN: adjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN; break;
                case FREE_LOSS: adjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_FREEROUND_LOSS; break;
                default: throw new Status422DataValidationError("Unhandled bet result kind: " + request.getKind());
            }
            BalanceAdjustmentRequest casinoRequest = BalanceAdjustmentRequest.builder()
                    .roundId(request.getRoundId())
                    .transactionId(context.getBetRound().getGuid() + ":" + context.getBetResult().getBetResultTransactionId())
                    .externalTimestamp(request.getTransactionTimestamp())
                    .gameGuid(context.getDomainName() + "/" + moduleInfo.getModuleName() + "_" + context.getBetRound().getGame().getGuid())
                    .sessionId(context.getBetRound().getSessionId())
                    .roundFinished(request.isRoundComplete())
                    .bonusTran(false)
                    .bonusId(-1)
                    .currencyCode(request.getCurrencyCode())
                    .userGuid(context.getBetRound().getUser().getGuid())
                    .realMoneyOnly(true)
                    .gameSessionId(context.getBetRound().getGuid())
                    .performAccessChecks(false)
                    .persistRound(true)
                    .shouldPerformConstraintValidations(false)
                    .build();

            casinoRequest.setDomainName(context.getDomainName());
            casinoRequest.setProviderGuid(context.getDomainName() + "/" + moduleInfo.getModuleName());

            ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();

            BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
                    .betTransactionId(context.getBetResult().getBetResultTransactionId())
                    .amount(returns)
                    .adjustmentType(adjustmentComponentType)
                    .transactionIdLabelOverride(context.getBetResult().getBetResultTransactionId())
                    .additionalReference(null)
                    .build();

            adjustmentComponents.add(bac);
            casinoRequest.setAdjustmentComponentList(adjustmentComponents);
            String locale = cachingDomainClientService.domainLocale(context.getDomainName());
            BalanceAdjustmentResponse response = casinoService.multiBetV1(casinoRequest, locale);

            log.debug("Response from casino " + response);

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
                    // TODO Why is this a string?
                    String extSystemTransactionIdString = response.getAdjustmentResponseComponentList().stream().findFirst().orElse(null).getExtSystemTransactionId();
                    Long extSystemTransactionId = Long.parseLong(extSystemTransactionIdString);
                    context.getBetResult().setLithiumAccountingId(extSystemTransactionId);
                    context.getBetResult().setBalanceAfter(CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue());
                    break;
                }
            }

        } catch (ErrorCodeException ece) {
            throw ece;
        } finally {
            SW.stop();
            SW.start("betresult.casino.storeresult." + context.getBetResult().getBetResultTransactionId());
            betResultRepository.save(context.getBetResult());
            SW.stop();
        }
    }
}
