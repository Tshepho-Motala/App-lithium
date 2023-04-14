package lithium.service.casino.provider.evolution.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.evolution.model.request.authentication.UserAuthenticationRequest;
import lithium.service.casino.provider.evolution.model.response.AuthenticationResponse;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;

import java.io.UnsupportedEncodingException;

public interface StartGameService {
    String startGame(String token, String gameId, String domainName, boolean demoGame, String deviceChannel, String lang) throws UnsupportedEncodingException, Status550ServiceDomainClientException, Status512ProviderNotConfiguredException, Status411UserNotFoundException, LithiumServiceClientFactoryException, UserClientServiceFactoryException, UserNotFoundException, Status500InternalServerErrorException;

    AuthenticationResponse authenticateOnEvolution(UserAuthenticationRequest userAuthenticationRequest, String domainName);
}