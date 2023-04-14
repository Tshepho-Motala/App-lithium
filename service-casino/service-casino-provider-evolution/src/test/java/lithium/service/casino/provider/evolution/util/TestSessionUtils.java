package lithium.service.casino.provider.evolution.util;

import lithium.service.casino.provider.evolution.exception.Status;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;

import static lithium.service.casino.provider.evolution.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.evolution.constant.TestConstants.SESSION_KEY;

public class TestSessionUtils {

    public static final String CHECK_USER_PATH = "/api/check";

    public static final String AUTH_PARAM_NAME = "authToken";

    public static CheckUserRequest validCheckUserRequest() {
        return CheckUserRequest.builder()
                .sid(SESSION_KEY)
                .userId(OPERATOR_ACCOUNT_ID)
                .build();
    }

    public static CheckUserResponse validCheckUserResponse() {
        return CheckUserResponse
                .builder()
                .sid(SESSION_KEY)
                .status(Status.OK.toString())
                .build();
    }
}
