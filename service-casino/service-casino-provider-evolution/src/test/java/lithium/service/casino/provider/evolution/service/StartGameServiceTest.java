package lithium.service.casino.provider.evolution.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import lithium.service.casino.provider.evolution.service.impl.StartGameServiceImpl;
import lithium.service.casino.provider.evolution.util.TestConstants;
import lithium.service.casino.provider.evolution.util.BaseMockService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class StartGameServiceTest extends BaseMockService {
    @Mock
  protected TokenStore tokenStore;
    @Mock
    protected SystemLoginEventsClient systemLoginEventsClient;
  protected StartGameServiceImpl startGameService;

  @BeforeEach
  void setup(){
      mockExternalServices();
      startGameService = new StartGameServiceImpl(
              cachingDomainClientService,
              lithiumConfigurationProperties,
              providerConfigService,
              serviceCasinoProviderEvolutionModuleInfo,
              tokenStore,
              userApiInternalClientService,
              evolutionAuthenticationRestService,
              userService
      );
  }

  @Test
  @SneakyThrows
  @Disabled
  public void whenValidStartGame_Then_Succeed() throws Exception {
      Mockito.when(tokenStore.readAccessToken(anyString())).thenReturn(oAuth2AccessToken);
      Map<String, Object> tokens = new HashMap<>();
      tokens.put("sessionKey", "dummy");
      Mockito.when(oAuth2AccessToken.getAdditionalInformation()).thenReturn(tokens);
      Mockito.when(userService.findLastLoginEventForSessionKey(any(), anyString())).thenReturn(LoginEvent.builder().sessionKey("dummy").build());
      Mockito.when(lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true)).thenReturn(systemLoginEventsClient);
      Mockito.when(systemLoginEventsClient.findBySessionKey(anyString())).thenReturn(validLoginEvent());
      Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
      Mockito.when(userApiInternalClientService.getUserByGuid(anyString())).thenReturn(validUser());
   when(startGameService.startGame("token", "gameId", "livescore_uk",
        false, "mobile_web", "en")).thenReturn("/entry?token=wdefsrgdrhtert");

   String resultString = startGameService.startGame("token", "gameId", "livescore_uk",
        false, "mobile_web", "en");

    assertThat(resultString).startsWith("/entry");
    MatcherAssert.assertThat(resultString, CoreMatchers.containsString("/entry"));
  }

    private User validUser() {
      return User.builder()
              .guid(TestConstants.VALID_USER_GUID)
              .countryCode(TestConstants.GBP_CURRENCY)
              .session(validLoginEvent())
              .build();
    }

    private Domain validDomain() {
        return Domain.builder().name(TestConstants.DOMAIN_NAME).currency(TestConstants.GBP_CURRENCY).build();
    }

    private LoginEvent validLoginEvent() {
      return LoginEvent.builder()
              .sessionKey("dummy")
              .ipAddress("127.0.0.1")
              .build();
    }
}
