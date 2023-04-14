package lithium.service.accountinghistory.controllers.admin;

import lithium.exceptions.Status425DateParseException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accountinghistory.service.AccountingService;
import lithium.service.accountinghistory.service.RewardsService;
import lithium.service.accountinghistory.service.ExternalDataService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/transactions")
public class AdminTransactionsController {
    @Autowired AccountingService accountingService;
    @Autowired RewardsService rewardsService;

    @Autowired
    ExternalDataService enrichDataService;
    @Autowired CachingDomainClientService cachingDomainClientService;
    @Autowired LithiumServiceClientFactory services;

    @PostMapping(value = "/table")
    public DataTableResponse<TransactionEntryBO> table(
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid", required = false) String userGuid,
            @RequestParam(name = "transactionId", required = false) String transactionId,
            @RequestParam(name = "draw") String draw,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "search", required = false) String searchValue,
            @RequestParam(name = "providerTransId", required = false) String providerTransId,
            @RequestParam(name = "providerGuid", required = false) String providerGuid,
            @RequestParam(name = "transactionType", required = false) List<String> transactionType,
            @RequestParam(name = "additionalTransId", required = false) String additionalTransId,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "domainName", required = false) String domainName,
            @RequestParam(name = "accountCode", required = false) String accountCode,
            @RequestParam(name = "roundId", required = false) String roundId,
            LithiumTokenUtil tokenUtil
    ) throws Status425DateParseException, Status510AccountingProviderUnavailableException {
        log.debug("Transactions table requested [dateRangeStart=" + dateRangeStart
                + ", dateRangeEnd=" + dateRangeStart + ", userGuid=" + userGuid + ", transactionId=" + transactionId
                + ", roundId=" + roundId + ", draw=" + draw + ", start=" + start + ", length=" + length + ", search=" + searchValue + "]");

        if (length > 100) length = 100;

        DataTableResponse<TransactionEntryBO> result = accountingService.adminTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid,
                transactionId, draw, start, length, searchValue, providerGuid, providerTransId, transactionType, additionalTransId,
                orderDirection, tokenUtil.getAccessToken().getValue(), domainName, accountCode, roundId);

        enrichDataService.enrichTransactionExternalData(result);

        Map<String, Game> domainGameMap = queryAllDomainGames();
        enrichDataService.enrichTransactionGames(domainGameMap, result);
        rewardsService.enrichRewardInformation(domainGameMap, result);

        return result;
    }


    private Map<String, Game> queryAllDomainGames() {
        HashMap<String, Game> domainGameMap = new HashMap<>();
        try {
            //retrieve domain list
            DomainClient domainClient = cachingDomainClientService.getDomainClient();
            Iterable<Domain> allDomains = domainClient.findAllDomains().getData();

            //retrieve game list per domain
            for (Domain domain : allDomains) {
                GamesClient gamesClient = services.target(GamesClient.class, "service-games", true);
                gamesClient.listDomainGames(domain.getName()).getData().forEach(game ->
                        domainGameMap.put(domain.getName() + "/" + game.getGuid(), game)
                );
            }
        } catch (Exception e) {
            log.error("Unable to build domain game list", e);
        }
        return domainGameMap;
    }

}

