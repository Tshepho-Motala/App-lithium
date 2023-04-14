package lithium.service.casino.provider.evolution.util;

import lithium.service.Response;
import lithium.service.casino.provider.evolution.ServiceCasinoProviderEvolutionModuleInfo;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationProperties;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationService;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.casino.provider.evolution.service.impl.UserServiceImpl;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static lithium.service.casino.provider.evolution.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.evolution.constant.TestConstants.MODULE_NAME;
import static lithium.service.casino.provider.evolution.constant.TestConstants.SESSION_KEY;
import static org.mockito.ArgumentMatchers.any;

@UtilityClass
public class TestMockUtils {

    private LithiumServiceClientFactory LITHIUM_SERVICE_CLIENT_FACTORY = Mockito.mock(LithiumServiceClientFactory.class);
    public ProviderClient PROVIDER_CLIENT = Mockito.mock(ProviderClient.class);

    private static final ProviderConfigurationService PROVIDER_CONFIG_SERVICE = new ProviderConfigurationService(LITHIUM_SERVICE_CLIENT_FACTORY,
            Mockito.mock(
                    ServiceCasinoProviderEvolutionModuleInfo.class));
    private LithiumServiceClientFactory lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);

    public static final SecurityConfigUtils SECURITY_CONFIG_UTILS = new SecurityConfigUtils(PROVIDER_CONFIG_SERVICE);
    public static final UserService USER_SERVICE = new UserServiceImpl(lithiumServiceClientFactory);

    @SneakyThrows
    public static SystemLoginEventsClient setUpSystemLoginEventsClientMock() {
        SystemLoginEventsClient systemLoginEventsClientMock = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.when(lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true)).thenReturn(systemLoginEventsClientMock);
        return systemLoginEventsClientMock;
    }


    public static void mockSuccessEvolutionConfig() {
        mockSuccessProviderClient();
        mockSuccessFindByUrlAndDomainName();
        mockSuccessPropertiesByProviderUrlAndDomainName(validProviderProperties());
    }

    public static void mockSuccessPropertiesByProviderUrlAndDomainName(List<ProviderProperty> validProviderProperties) {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(validProviderProperties).build();
        Mockito.doReturn(response).when(PROVIDER_CLIENT).propertiesByProviderUrlAndDomainName(any(), any());
    }

    public static List<ProviderProperty> validProviderProperties() {
        List<ProviderProperty> providerProperties = new ArrayList<>();
        providerProperties.add(ProviderProperty.builder()
                .name(ProviderConfigurationProperties.AUTH_TOKEN.getName())
                .provider(Provider.builder().enabled(true).name(MODULE_NAME).build())
                .value(AUTHORIZATION).build());
        providerProperties.add(ProviderProperty.builder()
                .name(ProviderConfigurationProperties.STARTGAME_BASE_URL.getName())
                .provider(Provider.builder().enabled(true).name(MODULE_NAME).build())
                .value("").build()
        );
        providerProperties.add(ProviderProperty.builder()
                .name(ProviderConfigurationProperties.EVOLUTION_AUTH_ENDPOINT.getName())
                .provider(Provider.builder().enabled(true).name(MODULE_NAME).build())
                .value("").build()
        );
        return providerProperties;
    }

    public static void mockSuccessFindByUrlAndDomainName() {
        Response<Object> response = Response.builder().status(Response.Status.OK).data(Provider.builder().enabled(true).build()).build();
        Mockito.doReturn(response).when(PROVIDER_CLIENT).findByUrlAndDomainName(any(), any());
    }

    @SneakyThrows
    public static void mockSuccessProviderClient() {
        Mockito.when(LITHIUM_SERVICE_CLIENT_FACTORY.target(ProviderClient.class, "service-domain", true)).thenReturn(PROVIDER_CLIENT);
    }

    @SneakyThrows
    public static LoginEvent setUpLoginEventMockBySessionKey() {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock();

        LoginEvent loginEventMock = Mockito.mock(LoginEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(systemLoginEventsClientMock.findBySessionKey(SESSION_KEY)).thenReturn(loginEventMock);
        Mockito.when(lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true)).thenReturn(systemLoginEventsClientMock);
        return loginEventMock;
    }


}
