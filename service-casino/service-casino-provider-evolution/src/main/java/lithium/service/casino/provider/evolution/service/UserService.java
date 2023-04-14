package lithium.service.casino.provider.evolution.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.context.GameRoundContext;
import lithium.service.user.client.objects.LoginEvent;

public interface UserService {
    String getUserGuid(LoginEvent loginEvent);
    void validateUserSession(String userGuid, String sid) throws Status500InternalServerErrorException;
    String getDomainFromPlayerId(String playerId);
    LoginEvent findLastLoginEventForSessionKey(GameRoundContext context, String sessionKey) throws Status500InternalServerErrorException;
}
