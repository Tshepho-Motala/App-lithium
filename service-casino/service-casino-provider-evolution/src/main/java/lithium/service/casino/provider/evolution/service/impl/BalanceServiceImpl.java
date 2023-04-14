package lithium.service.casino.provider.evolution.service.impl;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.provider.evolution.model.request.BalanceRequest;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.casino.provider.evolution.service.BalanceService;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.casino.provider.evolution.util.SecurityConfigUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@AllArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final CachingDomainClientService cachingDomainClientService;
    private final CasinoClientService casinoClientService;
    private final SecurityConfigUtils securityConfigUtils;
    private final UserService userService;

    @Override
    public StandardResponse getBalance(BalanceRequest balanceRequest, String authToken) throws Status411UserNotFoundException, Status500InternalServerErrorException {
        String domainName = userService.getDomainFromPlayerId(balanceRequest.getUserId());
        securityConfigUtils.validateAuthToken(authToken, domainName);
        userService.validateUserSession(balanceRequest.getUserId(), balanceRequest.getSid());
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        BigDecimal balance = casinoClientService.getPlayerBalance(
                domain.getName(),
                balanceRequest.getSid(),
                domain.getCurrency()
        ).getBalance();
        return StandardResponse.builder()
                .uuid(balanceRequest.getUuid())
                .bonus(new BigDecimal("0.00"))
                .retransmission(false)
                .status(HttpStatus.OK.name())
                .balance(balance.setScale(2, RoundingMode.HALF_EVEN))
                .build();
    }
}
