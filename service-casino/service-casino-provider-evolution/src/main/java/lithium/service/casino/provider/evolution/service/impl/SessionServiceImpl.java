package lithium.service.casino.provider.evolution.service.impl;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.exception.Status;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;
import lithium.service.casino.provider.evolution.service.SessionService;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.casino.provider.evolution.util.SecurityConfigUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SecurityConfigUtils securityConfigUtils;
    private final UserService userService;

    public CheckUserResponse checkUser(CheckUserRequest checkUserRequest, String authToken) throws Status500InternalServerErrorException {
        log.debug("Check user request: request=" + checkUserRequest);
        userService.validateUserSession(checkUserRequest.getUserId(), checkUserRequest.getSid());

        String domainName = userService.getDomainFromPlayerId(checkUserRequest.getUserId());
        securityConfigUtils.validateAuthToken(authToken, domainName);

        CheckUserResponse checkUserResponse = CheckUserResponse.builder()
                .sid(checkUserRequest.getSid())
                .status(Status.OK.toString())
                .build();

        log.debug("Check user response: response=" + checkUserResponse);

        return checkUserResponse;
    }

}
