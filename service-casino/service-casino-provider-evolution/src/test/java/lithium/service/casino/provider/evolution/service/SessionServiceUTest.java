package lithium.service.casino.provider.evolution.service;

import lithium.service.casino.provider.evolution.exception.Status10002InvalidParameterException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidSIDException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidTokenIdException;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;
import lithium.service.casino.provider.evolution.service.impl.SessionServiceImpl;
import lithium.service.casino.provider.evolution.util.TestSessionUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static lithium.service.casino.provider.evolution.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.evolution.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.USER_SERVICE;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.mockSuccessEvolutionConfig;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.setUpLoginEventMockBySessionKey;
import static lithium.service.casino.provider.evolution.util.TestMockUtils.setUpSystemLoginEventsClientMock;
import static lithium.service.casino.provider.evolution.util.TestSessionUtils.validCheckUserRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({MockitoExtension.class})
class SessionServiceUTest {

    private SessionService sessionService;

    @Mock
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl(SECURITY_CONFIG_UTILS, USER_SERVICE);
    }

    @Test
    @SneakyThrows
    void checkUser_ReturnsSuccessResponse_WhenRequestIsValid() {
        mockSuccessEvolutionConfig();
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getUser().getGuid())
                        .thenReturn(OPERATOR_ACCOUNT_ID);

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        CheckUserResponse actual = sessionService.checkUser(TestSessionUtils.validCheckUserRequest(), AUTHORIZATION);

        assertEquals(TestSessionUtils.validCheckUserResponse(), actual);
    }

//    @Test
//    void redeemToken_ThrowsSessionTokenExpiredException_WhenSessionTokenIsExpired() {
//        CheckUserRequest redeemSessionTokenRequest = TestSessionUtils.validRedeemSessionTokenRequest();
//        redeemSessionTokenRequest.setSessionToken(TestSessionUtils.getExpiredSessionToken());
//
//        Assertions.assertThrows(SessionTokenExpiredException.class, () -> sessionService.r(redeemSessionTokenRequest, AUTHORIZATION, WHITELISTED_IP));
//    }

    @Test
    void checkUser_ThrowsStatus10003InvalidTokenIdException_WhenNoAuthToken() {
        mockSuccessEvolutionConfig();
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();
        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);
        Mockito.when(loginEventMock.getUser().getGuid())
                .thenReturn(OPERATOR_ACCOUNT_ID);

        Assertions.assertThrows(Status10003InvalidTokenIdException.class, () -> sessionService.checkUser(validCheckUserRequest(), null));
    }

    @Test
    void checkUser_ThrowsStatus10003InvalidSIDException_WhenRequestDoesNotContainSid() {
        CheckUserRequest checkUserRequest = TestSessionUtils.validCheckUserRequest();
        checkUserRequest.setSid(null);
        Assertions.assertThrows(Status10003InvalidSIDException.class, () -> sessionService.checkUser(checkUserRequest, AUTHORIZATION));
    }

    @Test
    void checkUser_ThrowsStatus10003InvalidSIDException_WhenInvalidSid() {
        SystemLoginEventsClient systemLoginEventsClient = setUpSystemLoginEventsClientMock();
        Mockito.when(systemLoginEventsClient.findBySessionKey(anyString()))
               .thenThrow(Status412LoginEventNotFoundException.class);
        Assertions.assertThrows(Status10003InvalidSIDException.class, () -> sessionService.checkUser(validCheckUserRequest(), AUTHORIZATION));
    }

    @Test
    void checkUser_ThrowsStatus10003InvalidSIDException_WhenLoginEvenUserDoesNotExist() {
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        Mockito.when(loginEventMock.getUser())
                .thenReturn(null);
        Assertions.assertThrows(Status10003InvalidSIDException.class, () -> sessionService.checkUser(validCheckUserRequest(), AUTHORIZATION));
    }

    @Test
    void checkUser_ThrowsStatus10003InvalidSIDException_WhenLoginEvenUserGuidDoesNotExist() {
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        Mockito.when(loginEventMock.getUser().getGuid())
                .thenReturn(null);
        Assertions.assertThrows(Status10003InvalidSIDException.class, () -> sessionService.checkUser(validCheckUserRequest(), AUTHORIZATION));
    }

    @Test
    void checkUser_ThrowsStatus10002InvalidParameterException_WhenUserGuidIsNotProvided() {
        CheckUserRequest request = validCheckUserRequest();
        request.setUserId(null);

        Assertions.assertThrows(Status10002InvalidParameterException.class, () -> sessionService.checkUser(request, AUTHORIZATION));
    }

    @Test
    void checkUser_ThrowsStatus10002InvalidParameterException_WhenUserGuidDoesNotMatchSessionUser() {
        LoginEvent loginEventMock = setUpLoginEventMockBySessionKey();

        Mockito.when(loginEventMock.getLogout())
                .thenReturn(null);

        Mockito.when(loginEventMock.getUser().getGuid())
                .thenReturn(OPERATOR_ACCOUNT_ID);

        CheckUserRequest request = validCheckUserRequest();
        request.setUserId("domain/invalid");

        Assertions.assertThrows(Status10002InvalidParameterException.class, () -> sessionService.checkUser(request, AUTHORIZATION));
    }

}