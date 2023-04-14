package lithium.service.casino.provider.evolution.util;


import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.provider.evolution.ServiceCasinoProviderEvolutionModuleInfo;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationService;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.casino.provider.evolution.service.auth.EvolutionAuthenticationRestService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class BaseMockService {


    protected LithiumServiceClientFactory lithiumServiceClientFactory;
    protected CachingDomainClientService cachingDomainClientService;
    protected CasinoClientService casinoClientService;
    protected ServiceCasinoProviderEvolutionModuleInfo serviceCasinoProviderEvolutionModuleInfo;
    protected LithiumConfigurationProperties lithiumConfigurationProperties;
    protected LimitInternalSystemService limitInternalSystemService;
    protected ProviderConfigurationService providerConfigService;
    protected OAuth2AccessToken oAuth2AccessToken;
    protected EvolutionAuthenticationRestService evolutionAuthenticationRestService;
    protected UserService userService;
    protected UserApiInternalClientService userApiInternalClientService;
    @SneakyThrows
    protected void mockExternalServices(){
        cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
        lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);
        casinoClientService = Mockito.mock(CasinoClientService.class);
        serviceCasinoProviderEvolutionModuleInfo = Mockito.mock(ServiceCasinoProviderEvolutionModuleInfo.class);
        lithiumConfigurationProperties = Mockito.mock(LithiumConfigurationProperties.class);
        limitInternalSystemService = Mockito.mock(LimitInternalSystemService.class);
        providerConfigService = Mockito.mock(ProviderConfigurationService.class);
        oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        userService = Mockito.mock(UserService.class);
        evolutionAuthenticationRestService = Mockito.mock(EvolutionAuthenticationRestService.class);
        userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);
    }
}
