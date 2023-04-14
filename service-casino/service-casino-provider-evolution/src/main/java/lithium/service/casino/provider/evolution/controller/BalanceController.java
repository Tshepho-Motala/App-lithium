package lithium.service.casino.provider.evolution.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.model.request.BalanceRequest;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.casino.provider.evolution.service.BalanceService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping
    public StandardResponse getBalance(@RequestBody BalanceRequest balanceRequest, @RequestParam("authToken") String authToken) throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
        return balanceService.getBalance(balanceRequest, authToken);
    }

}
