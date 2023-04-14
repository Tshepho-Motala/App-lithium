package lithium.service.accountinghistory.service;

import com.google.common.collect.ImmutableList;
import lithium.exceptions.ErrorCodeException;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.client.objects.Game;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ExternalDataService {
    private CasinoClientService casinoClientService;
    private final List<String> CASHIER_TRANSACTION_TYPES = ImmutableList.of("CASHIER_DEPOSIT", "CASHIER_PAYOUT");

    public DataTableResponse<TransactionEntryBO> enrichTransactionExternalData(DataTableResponse<TransactionEntryBO> result) {
        enrichCasinoDetails(result);
        enrichCashierDetails(result);
        return result;
    }

    private void enrichCashierDetails(DataTableResponse<TransactionEntryBO> result) {
        try {
            for (TransactionEntryBO entryBO : result.getData()) {
                if (CASHIER_TRANSACTION_TYPES.contains(entryBO.getTransaction().getTransactionType().getCode())) {
                    entryBO.getDetails().setExternalTransactionDetailUrl("CashierTransactionID:" + entryBO.getDetails().getExternalTranId());
                }
            }
        } catch (Exception e) {
            log.error("Can't update with cashier data: " + e.getMessage());
        }
    }

    private void enrichCasinoDetails(DataTableResponse<TransactionEntryBO> result) {
        List<TransactionDetailPayload> queryData = result.getData().stream()
                .filter(c -> c.getDetails() != null && c.getDetails().getProviderGuid() != null)
                .map(c -> TransactionDetailPayload.builder()
                        .providerGuid(
                                c.getDetails().getProviderGuid().contains("/")
                                        ? c.getDetails().getProviderGuid()
                                        : c.getAccount().getDomain().getName() + "/" + c.getDetails().getProviderGuid()
                        )
                        .transactionType(c.getTransaction().getTransactionType().getCode())
                        .providerTransactionGuid(c.getDetails().getExternalTranId()) // todo provider tran id - to confirm
                        .build())
                .collect(Collectors.toList());

        log.debug("AdminTransactionsController query: " + queryData);

        try {
            final List<TransactionDetailPayload> detailResponseList = casinoClientService.findTransactionDetailUrls(queryData);

            detailResponseList.forEach(p -> {
                log.debug("AdminTransactionsController->table: " + p.toString());
            });

            detailResponseList.forEach(detailResponse -> {
                result.getData()
                        .stream()
                        .filter(playerTran -> playerTran.getDetails().getExternalTranId() != null)
                        .filter(playerTran -> playerTran.getDetails().getExternalTranId().contentEquals(detailResponse.getProviderTransactionGuid()))
                        .filter(playerTran -> playerTran.getDetails().getExternalTransactionDetailUrl() == null)
                        .filter(playerTran -> playerTran.getTransaction().getTransactionType().getCode().equalsIgnoreCase(detailResponse.getTransactionType()))
                        .findFirst()
                        .ifPresent(matchedTran -> {
                            matchedTran.getDetails().setExternalTransactionDetailUrl(detailResponse.getTransactionDetailUrl());
                        });
            });

        } catch (ErrorCodeException e) {
            log.debug("Problem looking up transaction details: " + e.getMessage(), e);
        } catch (Exception e) {
            log.info("Problem looking up transaction details: " + e.getMessage(), e);
        }
    }
    public void enrichTransactionGames(Map<String, Game> domainGameMap, DataTableResponse<TransactionEntryBO> result) {
        //loop through results and allocate
        result.getData().stream()
                .filter(t -> t.getDetails().getGameGuid() != null)
                .forEach(
                        transactionEntryBO -> {
                            String gameName = findGameName(
                                    transactionEntryBO.getAccount().getDomain().getName(),
                                    transactionEntryBO.getDetails().getGameGuid(),
                                    domainGameMap
                            );

                            transactionEntryBO.getDetails().setGameName(gameName);
                        }
                );
    }

    private String findGameName(String domainName, String gameGuid, Map<String, Game> domainGameMap) {
        String domainGameKey = (gameGuid!=null&&(gameGuid.startsWith(domainName+"/")))?gameGuid:domainName + "/" + gameGuid;

        if (domainGameMap.containsKey(domainGameKey)) {
            return domainGameMap.get(domainGameKey).getName();
        }

        return null;
    }
}
