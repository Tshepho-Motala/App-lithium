package lithium.service.casino.provider.evolution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.CustomHttpErrorCodeControllerAdvice;
import lithium.service.casino.provider.evolution.exception.Status;
import lithium.service.casino.provider.evolution.exception.handler.EvolutionControllerAdvice;
import lithium.service.casino.provider.evolution.model.request.BalanceRequest;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lithium.service.casino.provider.evolution.service.BalanceMockService;
import lithium.service.casino.provider.evolution.service.BalanceService;
import lithium.service.casino.provider.evolution.service.impl.BalanceServiceImpl;
import lithium.service.user.client.objects.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static lithium.service.casino.provider.evolution.util.TestConstants.BALANCE_PATH;
import static lithium.service.casino.provider.evolution.util.TestConstants.BALANCE_PROPERTY_NAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({MockitoExtension.class})
public class BalanceControllerUTest extends BalanceMockService {

    private MockMvc mvc;

    @BeforeEach
    void setup(){
        mockExternalServices();
        BalanceService balanceService = new BalanceServiceImpl(cachingDomainClientService, casinoClientService, securityConfigUtils, userService);
        BalanceController balanceController = new BalanceController(balanceService);
        mvc = MockMvcBuilders.standaloneSetup(balanceController)
                .setControllerAdvice(new CustomHttpErrorCodeControllerAdvice())
                .setControllerAdvice(new EvolutionControllerAdvice())
                .addPlaceholderValue(BALANCE_PROPERTY_NAME, BALANCE_PATH)
                .build();
    }

    @Test
    @SneakyThrows
    @Disabled
    void whenValidBalanceRequest_Then_Succeed(){
        mockSuccessEvolutionConfig();
        mockSuccessGetLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
                .thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
                .thenReturn(validCasinoClientBalanceResponse("15000"));
        Mockito.when(loginEvent.getUser()).thenReturn(User.builder().guid("livescore_uk/123456789").build());
        Mockito.when(userService.getUserGuid(loginEvent)).thenReturn("livescore_uk/123456789");
        BalanceRequest balanceRequest = validBalanceRequest();
        String authToken = "s3cr3tV4lu3";
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(balanceRequest);
        StandardResponse standardResponse = validResponse("150.00");
        String expectedResponse = objectMapper.writeValueAsString(standardResponse);
        mvc.perform(
                post(BALANCE_PATH + "?authToken=" + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        ).andExpect(status().isOk())
                .andExpect(content().json(expectedResponse)).andDo(print());
    }

    @Test
    @SneakyThrows
    void whenInvalidBalanceRequestSIDNotExistent_then_FailWithStatus10003InvalidSIDException(){
        mockSuccessEvolutionConfig();
        mockFailureGetLastLoginEvent();
        BalanceRequest balanceRequest = invalidBalanceRequestInvalidSID();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(balanceRequest);
        String authToken = "s3cr3tV4lu3";
        mvc.perform(
                post(BALANCE_PATH + "?authToken=" + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_SID.name()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void whenInvalidBalanceRequestNonExistentToken_then_FailWithStatus10003InvalidTokenException(){
        mockSuccessEvolutionConfig();
        BalanceRequest balanceRequest = validBalanceRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(balanceRequest);
        String authToken = "s3cr3tV4lu3gsbshdnm";
        mvc.perform(
                post(BALANCE_PATH + "?authToken=" + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_TOKEN_ID.name()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void whenInvalidBalanceRequestInvalidTokenTooLong_then_FailWithStatus10003InvalidTokenException(){
        mockSuccessEvolutionConfig();
        BalanceRequest balanceRequest = validBalanceRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(balanceRequest);
        String authToken = "s3cr3tV4lu3bssh273bx7212b3-_hddg354bxsgd32235~fbdvuersjxvcwncdscdhvfvhfbvf";
        mvc.perform(
                    post(BALANCE_PATH + "?authToken=" + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_TOKEN_ID.name()))
                .andDo(print());
    }
}
