package lithium.service.casino.provider.evolution.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.provider.evolution.configuration.ProviderConfiguration;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidSIDException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidTokenIdException;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.casino.provider.evolution.service.impl.BalanceServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceUTest extends BalanceMockService{

    BalanceService balanceService;

    ObjectMapper objectMapper;
    ProviderConfiguration providerConfiguration;

    @BeforeEach
    void setup() {
        mockExternalServices();
        objectMapper = new ObjectMapper();
        balanceService = new BalanceServiceImpl(cachingDomainClientService, casinoClientService, securityConfigUtils, userService);
        providerConfiguration = Mockito.mock(ProviderConfiguration.class);
    }

    @Test
    @SneakyThrows
    void whenValidBalanceRequest_Then_SuccessBalanceResponse(){
        mockSuccessGetLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
                .thenReturn(validDomain());
        Mockito.when(providerConfigurationService.getEvolutionProviderConfig(anyString()))
                        .thenReturn(
                                ProviderConfiguration.builder()
                                        .authToken("dummytoken")
                                        .build()
                        );
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
                .thenReturn(validCasinoClientBalanceResponse("10000"));

        StandardResponse expectedResponse = validResponse("100.00");
        StandardResponse actualResponse = balanceService.getBalance(validBalanceRequest(), "dummytoken");
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    void whenValidBalanceRequest_And_DomainDoesNotExist_Then_FailWithStatus550ServiceDomainClientException(){
        Mockito.when(providerConfigurationService.getEvolutionProviderConfig(anyString()))
                .thenReturn(
                        ProviderConfiguration.builder()
                                .authToken("dummytoken")
                                .build()
                );
        Status10003InvalidTokenIdException exception = Assertions.assertThrows(
                Status10003InvalidTokenIdException.class,
                () -> balanceService.getBalance(validBalanceRequest(), "fake-domain"));
        Assertions.assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void whenInvalid_SID_Then_Status10003InvalidSIDException() {
        mockFailureGetLastLoginEvent();
        Mockito.when(providerConfigurationService.getEvolutionProviderConfig(anyString()))
                .thenReturn(
                        ProviderConfiguration.builder()
                                .authToken("dummytoken")
                                .build()
                );
        Status10003InvalidSIDException exception = Assertions.assertThrows(Status10003InvalidSIDException.class,
                () -> balanceService.getBalance(invalidBalanceRequestInvalidSID(), "dummytoken"));
        Assertions.assertEquals("Invalid sid: 6168fd11-7e67-4883-a828-fake userGuid: livescore_uk/123456789",exception.getMessage());
    }
}
