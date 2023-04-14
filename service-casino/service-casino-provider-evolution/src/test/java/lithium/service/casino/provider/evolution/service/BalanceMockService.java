package lithium.service.casino.provider.evolution.service;

import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.provider.evolution.ServiceCasinoProviderEvolutionModuleInfo;
import lithium.service.casino.provider.evolution.configuration.ProviderConfiguration;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationService;
import lithium.service.casino.provider.evolution.model.request.BalanceRequest;
import lithium.service.casino.provider.evolution.model.request.Details;
import lithium.service.casino.provider.evolution.model.request.Game;
import lithium.service.casino.provider.evolution.model.request.Table;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.casino.provider.evolution.service.impl.UserServiceImpl;
import lithium.service.casino.provider.evolution.util.SecurityConfigUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static lithium.service.casino.provider.evolution.util.TestConstants.VALID_AUTH_TOKEN;
import static org.mockito.ArgumentMatchers.anyString;

public class BalanceMockService {

    protected CachingDomainClientService cachingDomainClientService;
    protected CasinoClientService casinoClientService;
    protected LithiumServiceClientFactory lithiumServiceClientFactory;

    protected ProviderConfigurationService providerConfigurationService;

    protected ServiceCasinoProviderEvolutionModuleInfo serviceCasinoProviderEvolutionModuleInfo;
    protected UserService userService;

    protected SecurityConfigUtils securityConfigUtils;
    protected LoginEvent loginEvent =  Mockito.mock(LoginEvent.class);

    public static UUID VALID_UUID = UUID.randomUUID();

    @SneakyThrows
    protected void mockExternalServices(){
        casinoClientService = Mockito.mock(CasinoClientService.class);
        cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
        lithiumServiceClientFactory = Mockito.mock(LithiumServiceClientFactory.class);
        serviceCasinoProviderEvolutionModuleInfo = new ServiceCasinoProviderEvolutionModuleInfo();
        providerConfigurationService = Mockito.mock(ProviderConfigurationService.class);
        userService = new UserServiceImpl(lithiumServiceClientFactory);
        securityConfigUtils = new SecurityConfigUtils(providerConfigurationService);

    }

    protected static BalanceRequest validBalanceRequest(){
        return BalanceRequest.builder()
                .sid("6168fd11-7e67-4883-a828-fc33949d74bf")
                .userId("livescore_uk/123456789")
                .currency("GBP")
                .uuid(VALID_UUID.toString())
                .game(Game.builder()
                        .id(null)
                        .type("blackjack")
                        .details(Details.builder()
                                .table(Table.builder()
                                        .id(UUID.randomUUID().toString())
                                        .vid(UUID.randomUUID().toString())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    protected static BalanceRequest invalidBalanceRequestInvalidSID(){
        return BalanceRequest.builder()
                .sid("6168fd11-7e67-4883-a828-fake")
                .userId("livescore_uk/123456789")
                .currency("GBP")
                .uuid(VALID_UUID.toString())
                .game(Game.builder()
                        .id(null)
                        .type("blackjack")
                        .details(Details.builder()
                                .table(Table.builder()
                                        .id(UUID.randomUUID().toString())
                                        .vid(UUID.randomUUID().toString())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    public static Domain validDomain(){
        return Domain.builder().name("livescore_uk").currency("GBP").build();
    }

    @SneakyThrows
    public void mockFailureGetLastLoginEvent() {
        SystemLoginEventsClient loginEventsClient = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.doReturn(loginEventsClient).when(lithiumServiceClientFactory).target(SystemLoginEventsClient.class, "service-user", true);
        Mockito.when(loginEventsClient.findBySessionKey(ArgumentMatchers.anyString())).thenThrow(new Status412LoginEventNotFoundException("user is invalid"));
    }

    @SneakyThrows
    public void mockSuccessGetLoginEvent() {
        SystemLoginEventsClient systemLoginEventsClient = Mockito.mock(SystemLoginEventsClient.class);
        Mockito.doReturn(systemLoginEventsClient).when(lithiumServiceClientFactory).target(SystemLoginEventsClient.class, "service-user", true);
        Mockito.when(systemLoginEventsClient.findBySessionKey(ArgumentMatchers.anyString())).thenReturn(
                LoginEvent.builder()
                        .user(User.builder()
                                .guid("livescore_uk/123456789")
                                .build())
                        .sessionKey("6168fd11-7e67-4883-a828-fc33949d74bf")
                        .build()
        );
    }

    public void mockSuccessEvolutionConfig() {
        mockGetProviderConfig();
    }

    private void mockGetProviderConfig(){
        Mockito.doReturn(ProviderConfiguration.builder().authToken(VALID_AUTH_TOKEN).build())
                .when(providerConfigurationService)
                .getEvolutionProviderConfig(anyString());
    }

    protected BalanceResponse validCasinoClientBalanceResponse(String cents) {
        return BalanceResponse.builder().balanceCents(Long.valueOf(cents)).build();
    }

    protected StandardResponse validResponse(String balance) {
        return StandardResponse.builder()
                .balance(new BigDecimal(balance))
                .status("OK")
                .bonus(new BigDecimal("0.00"))
                .retransmission(false)
                .uuid(VALID_UUID.toString())
                .build();
    }

    protected void validProviderConfigurations(String authToken){
        Mockito.when(providerConfigurationService.getEvolutionProviderConfig(ArgumentMatchers.anyString())).thenReturn(
                ProviderConfiguration.builder()
                        .authToken(getAuthToken(authToken))
                        .build()
        );
    }

    protected void validProviderInvalidTokensConfigurations(String authToken){
        Mockito.when(providerConfigurationService.getEvolutionProviderConfig(ArgumentMatchers.anyString())).thenReturn(
                ProviderConfiguration.builder()
                        .authToken(getInvalidAuthToken(authToken))
                        .build()
        );
    }

    protected String getAuthToken(String authToken){
        List<String> tokens = Arrays.stream(new String[]{"s3cr-tV4_u3", "s3cr3tV4lu3", "s3cr3tV-_u3", "A9cd3tV4_u3", "A9_d~tV4_u3"}).toList();
        return tokens.stream().filter(token -> token.equals(authToken)).findFirst().orElse("");
    }

    protected String getInvalidAuthToken(String authToken){
        List<String> tokens = Arrays.stream(new String[]{"s3cr-tV4_*%==u3", "s3cr%$^3tV4lu3", "s3cr3*&^%tV-_u3", "A9cd3tV4_#$@u3", "A9_%^$$@^d~tV4_u3"}).toList();
        return tokens.stream().filter(token -> token.equals(authToken)).findFirst().orElse("");
    }

}
