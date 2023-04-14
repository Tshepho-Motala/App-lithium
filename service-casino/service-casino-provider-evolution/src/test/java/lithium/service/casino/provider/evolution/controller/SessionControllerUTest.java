package lithium.service.casino.provider.evolution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.provider.evolution.exception.Status;
import lithium.service.casino.provider.evolution.exception.handler.EvolutionControllerAdvice;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;
import lithium.service.casino.provider.evolution.service.SessionService;
import lithium.service.casino.provider.evolution.service.impl.SessionServiceImpl;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static lithium.service.casino.provider.evolution.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.evolution.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.USER_SERVICE;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.mockSuccessEvolutionConfig;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.setUpLoginEventMockBySessionKey;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.setUpSystemLoginEventsClientMock;
import static lithium.service.casino.provider.evolution.util.TestSessionUtils.AUTH_PARAM_NAME;
import static lithium.service.casino.provider.evolution.util.TestSessionUtils.CHECK_USER_PATH;
import static lithium.service.casino.provider.evolution.util.TestSessionUtils.validCheckUserRequest;
import static lithium.service.casino.provider.evolution.util.TestSessionUtils.validCheckUserResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class SessionControllerUTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;
    @BeforeEach
    void setUp() {
        SessionService sessionService = new SessionServiceImpl(SECURITY_CONFIG_UTILS, USER_SERVICE);
        SessionController sessionController = new SessionController(sessionService);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(sessionController)
                .setControllerAdvice(new EvolutionControllerAdvice())
                .build();
    }

    @Test
    @SneakyThrows
    void check_SuccessResponse_WhenRequestIsValid() {
        mockSuccessEvolutionConfig();
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getUser().getGuid())
                .thenReturn(OPERATOR_ACCOUNT_ID);

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        CheckUserRequest checkUserRequest = validCheckUserRequest();
        CheckUserResponse checkUserResponse = validCheckUserResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(checkUserRequest);
        String responseBody = objectMapper.writeValueAsString(checkUserResponse);


        mockMvc.perform(post(CHECK_USER_PATH)
                        .param(AUTH_PARAM_NAME, AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    @SneakyThrows
    void check_Status10003InvalidSIDException_whenInvalidSid(){
        CheckUserRequest checkUserRequest = validCheckUserRequest();
        checkUserRequest.setSid(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(checkUserRequest);
        mockMvc.perform(
                        post(CHECK_USER_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(AUTH_PARAM_NAME, AUTHORIZATION)
                                .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_SID.name()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void redeemToken_SessionNotFoundResponse_WhenLoginEventNotFound() {
        SystemLoginEventsClient systemLoginEventsClientMock = setUpSystemLoginEventsClientMock();

        Mockito.when(systemLoginEventsClientMock.findBySessionKey(anyString()))
                .thenThrow(Status412LoginEventNotFoundException.class);

        CheckUserRequest checkUserRequest = validCheckUserRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(checkUserRequest);
        mockMvc.perform(
                        post(CHECK_USER_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(AUTH_PARAM_NAME, AUTHORIZATION)
                                .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_SID.name()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void check_InvalidSid_WhenLoginEventDoesNotContainUser() {
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();
        Mockito.when(loginEventMock.getUser())
                .thenReturn(null);

        CheckUserRequest checkUserRequest = validCheckUserRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(checkUserRequest);
        mockMvc.perform(
                        post(CHECK_USER_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(AUTH_PARAM_NAME, AUTHORIZATION)
                                .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_SID.name()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void check_InvalidSid_WhenLoginEventUserDoesNotContainGuid() {
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        Mockito.when(loginEventMock.getUser().getGuid())
                .thenReturn(null);

        CheckUserRequest checkUserRequest = validCheckUserRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(checkUserRequest);
        mockMvc.perform(
                        post(CHECK_USER_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(AUTH_PARAM_NAME, AUTHORIZATION)
                                .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.INVALID_SID.name()))
                .andDo(print());
    }

}
