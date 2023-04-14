package lithium.service.casino.provider.evolution.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.evolution.ServiceCasinoProviderEvolutionModuleInfo;
import lithium.service.casino.provider.evolution.configuration.ProviderConfiguration;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationProperties;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationService;
import lithium.service.casino.provider.evolution.context.GameRoundContext;
import lithium.service.casino.provider.evolution.exception.Status401NotLoggedInException;
import lithium.service.casino.provider.evolution.model.request.authentication.*;
import lithium.service.casino.provider.evolution.model.response.AuthenticationFailureResponse;
import lithium.service.casino.provider.evolution.model.response.AuthenticationResponse;
import lithium.service.casino.provider.evolution.service.StartGameService;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.casino.provider.evolution.service.auth.EvolutionAuthenticationRestService;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.exceptions.Status502ProviderProcessingException;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartGameServiceImpl implements StartGameService {
  private final CachingDomainClientService cachingDomainClientService;
  private final LithiumConfigurationProperties lithiumConfigurationProperties;
  private final ProviderConfigurationService providerConfigService;
  private final ServiceCasinoProviderEvolutionModuleInfo evolutionModuleInfo;
  private final TokenStore tokenStore;
  private final UserApiInternalClientService userApiInternalClientService;
  private final EvolutionAuthenticationRestService evolutionAuthenticationRestService;
  private final UserService userService;

  @Override
  public String startGame(String token, String gameId, String domainName, boolean demoGame,
      String deviceChannel, String lang) throws
          UnsupportedEncodingException,
          Status550ServiceDomainClientException,
          Status512ProviderNotConfiguredException,
          Status411UserNotFoundException,
          UserNotFoundException,
          Status483PlayerCasinoNotAllowedException, Status500InternalServerErrorException, UserClientServiceFactoryException {
    LithiumTokenUtil lithiumTokenUtil = LithiumTokenUtil.builder(tokenStore, token).build();
    User user = userApiInternalClientService.getUserByGuid(lithiumTokenUtil.guid());
    LoginEvent loginEvent = userService.findLastLoginEventForSessionKey(
            new GameRoundContext(),
            getUserSessionKeyFromAuthToken(lithiumTokenUtil)
    );
    if(!demoGame){
      validateToken(lithiumTokenUtil, loginEvent);
    }

    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    ProviderConfiguration evolutionProviderConfig = providerConfigService.getEvolutionProviderConfig(domain.getName());
    UserAuthenticationRequest userAuthenticationRequest = getUserAuthenticationRequest(user, domain, gameId, loginEvent);
    AuthenticationResponse authenticationResponse = authenticateOnEvolution(userAuthenticationRequest, domainName);
    String redirectUrl = buildRedirectUrl(evolutionProviderConfig, authenticationResponse.getEntry());
    return buildGatewayPublicUrl(lithiumConfigurationProperties, evolutionModuleInfo, redirectUrl);
  }

  private UserAuthenticationRequest getUserAuthenticationRequest(User user, Domain domain, String gameId, LoginEvent loginEvent) {
      if(user != null){
        PlayerSession playerSession = PlayerSession.builder()
                .ip(loginEvent.getIpAddress())
                .id(loginEvent.getSessionKey())
                .build();
        Player player = Player.builder()
                .id(user.guid())
                .country(domain.getDefaultCountry())
                .currency(domain.getCurrency())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .update(false)
                .session(playerSession)
                .language(user.getCountryCode())
                .build();
        Channel channel = Channel.builder()
                .wrapped(false)
                .build();
        GameTable gameTable = GameTable.builder()
                .id(gameId)
                .build();
        PlayerGame playerGame = PlayerGame.builder()
                .table(gameTable)
                .build();
        GameConfiguration gameConfiguration = GameConfiguration.builder()
                .channel(channel)
                .game(playerGame)
                .build();
        UserAuthenticationRequest userAuthenticationRequest = UserAuthenticationRequest.builder()
                .uuid(UUID.randomUUID().toString())
                .player(player)
                .config(gameConfiguration)
                .build();
        return userAuthenticationRequest;
      }

    return null;
  }

  @Override
  public AuthenticationResponse authenticateOnEvolution(UserAuthenticationRequest userAuthenticationRequest, String domainName) {
    try{
      String url = providerConfigService.getEvolutionProviderConfig(domainName).getEvolutionAuthUrl();
      validateProviderConfigs(url);
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<UserAuthenticationRequest> requestEntity = new HttpEntity<>(userAuthenticationRequest, headers);
      ResponseEntity<String> response = evolutionAuthenticationRestService
              .getRestTemplate()
              .exchange(url, HttpMethod.POST, requestEntity, String.class);
      ObjectMapper mapper = new ObjectMapper();
      log.trace("Response :: {}", response);
      if(response != null && response.hasBody()){
        if(response.getStatusCode() == HttpStatus.OK){
          return mapper.readValue(response.getBody(), AuthenticationResponse.class);
        }
        if(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()){
          AuthenticationFailureResponse failureResponse = mapper.readValue(response.getBody(), AuthenticationFailureResponse.class);
          StringJoiner stringJoiner = new StringJoiner(",");
          failureResponse.getErrors().forEach(errors -> stringJoiner.add(errors.getCode() + " : " + errors.getMessage()));
          throw new Status502ProviderProcessingException(stringJoiner.toString());
        }
      }
    }catch (Status512ProviderNotConfiguredException status512ProviderNotConfiguredException){
      log.warn("Provider Evolution not configured for [domain : " + domainName + "] " + status512ProviderNotConfiguredException.getMessage());
      return null;
    }catch (IllegalArgumentException illegalArgumentException){
      log.warn("Provider property is missing evolution authentication url. [domain: " +  domainName + "] " + illegalArgumentException.getMessage());
      return null;
    } catch (JsonMappingException exception) {
      log.warn("Failed to deserialize Evolution response." + exception.getMessage());
      return null;
    } catch (JsonProcessingException exception) {
      log.warn("Failed to deserialize Evolution response." + exception.getMessage());
      return null;
    }
    return null;
  }

  private void validateProviderConfigs(String url) {
    List<String> missingProperties = new ArrayList<>();
    if (url == null || url.trim().isEmpty()) {
      missingProperties.add(ProviderConfigurationProperties.EVOLUTION_AUTH_ENDPOINT.getName());
    }

    if (!missingProperties.isEmpty()) {
      String missingPropertiesStr = String.join(", ", missingProperties);
      throw new IllegalArgumentException("One or more required configuration properties not set."
              + " ["+missingPropertiesStr+"]");
    }
  }

  private void validateToken(LithiumTokenUtil lithiumTokenUtil, LoginEvent loginEvent) throws Status500InternalServerErrorException {
    Date tokenExpiryDate = getSessionTokenExpiryDate(lithiumTokenUtil);
    if (tokenExpiryDate != null
            && tokenExpiryDate.before(Calendar.getInstance().getTime())
    ) {
      throw new Status401NotLoggedInException();
    }

    if (loginEvent == null) {
      throw new Status401NotLoggedInException();
    }
  }

  private String getUserSessionKeyFromAuthToken(LithiumTokenUtil lithiumTokenUtil) {
    String sessionKey = lithiumTokenUtil.getAccessToken().getAdditionalInformation().get("sessionKey").toString();
    return sessionKey;
  }

  private Date getSessionTokenExpiryDate(LithiumTokenUtil lithiumTokenUtil) {
    Date sessionExpiryDate = lithiumTokenUtil.getAccessToken().getExpiration();
    return sessionExpiryDate;
  }
  private static String buildGatewayPublicUrl(LithiumConfigurationProperties lithiumConfigurationProperties,
      ServiceCasinoProviderEvolutionModuleInfo evolutionModuleInfo, String redirectUrl) {
    return UriComponentsBuilder.fromUriString(lithiumConfigurationProperties.getGatewayPublicUrl())
            .path(evolutionModuleInfo.getModuleName())
            .path("/#!")
            .queryParam("url", redirectUrl)
            .build().toUriString();
  }

  private String buildRedirectUrl(ProviderConfiguration providerConfiguration, String evolutionUrl) throws UnsupportedEncodingException {
    UriComponentsBuilder redirectUrl = UriComponentsBuilder.fromUriString(providerConfiguration.getStartGameUrl() + evolutionUrl);
    return URLEncoder.encode(redirectUrl.build().toUriString(), StandardCharsets.UTF_8.name());
  }
}
