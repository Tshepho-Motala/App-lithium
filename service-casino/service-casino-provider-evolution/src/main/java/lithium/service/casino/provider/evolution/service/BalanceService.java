package lithium.service.casino.provider.evolution.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.model.request.BalanceRequest;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.client.LithiumServiceClientFactoryException;

public interface BalanceService {
    StandardResponse getBalance(BalanceRequest balanceRequest, String authToken) throws LithiumServiceClientFactoryException, Status500InternalServerErrorException;
}
