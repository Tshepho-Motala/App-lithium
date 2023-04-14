package lithium.service.casino.provider.evolution.service.impl;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.context.GameRoundContext;
import lithium.service.casino.provider.evolution.exception.InternalServerErrorException;
import lithium.service.casino.provider.evolution.exception.Status10002InvalidParameterException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidSIDException;
import lithium.service.casino.provider.evolution.exception.Status401NotLoggedInException;
import lithium.service.casino.provider.evolution.exception.UpstreamValidationFailedException;
import lithium.service.casino.provider.evolution.service.UserService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final LithiumServiceClientFactory lithiumServiceClientFactory;
    @Override
    public String getUserGuid(LoginEvent loginEvent) {
        return Optional.ofNullable(loginEvent.getUser())
                .map(User::getGuid)
                .orElseThrow(() -> new UpstreamValidationFailedException("Can't retrieve userGuid from loginEvent=" + loginEvent));
    }

    @Override
    public void validateUserSession(String userGuid, String sid) throws Status500InternalServerErrorException {
        if (sid == null) {
            throw new Status10003InvalidSIDException("sid not provided");
        }

        if(userGuid == null) {
            throw new Status10002InvalidParameterException("userGuid not provided");
        }

        try {
            SystemLoginEventsClient systemLoginEventsClient = getSystemLoginEventsClient();
            LoginEvent loginEvent = systemLoginEventsClient.findBySessionKey(sid);

            if (loginEvent.getLogout() != null) {
                throw new Status10003InvalidSIDException("Session closed. LoginEvent" + loginEvent + " sid: "  + sid + " userGuid: " + userGuid);
            }
            if (!getUserGuid(loginEvent).equals(userGuid)) {
                throw new Status10002InvalidParameterException("userGuid: " + userGuid + " does not match loginEvent userGuid: " + loginEvent.getUser().getGuid());
            }
        } catch (Status10002InvalidParameterException e) {
            throw e;
        } catch (Status412LoginEventNotFoundException | Status10003InvalidSIDException e) {
            throw new Status10003InvalidSIDException("Invalid sid: " + sid + " userGuid: " + userGuid);
        } catch (UpstreamValidationFailedException e) {
            throw new Status10003InvalidSIDException("Failed on user data validation: sid: "  + sid + " userGuid: " + userGuid, e);
        } catch (Exception e) {
            throw new Status500InternalServerErrorException("Unknown Error Exception. sid: "  + sid + " userGuid: " + userGuid, e);
        }

    }

    @Override
    public String getDomainFromPlayerId(String playerId) {
        return playerId.split("/")[0];
    }

    @Override
    public LoginEvent findLastLoginEventForSessionKey(GameRoundContext context, String sessionKey) throws Status500InternalServerErrorException {
        try{
            SystemLoginEventsClient systemLoginEventsClient = getSystemLoginEventsClient();
            LoginEvent loginEvent = systemLoginEventsClient.findBySessionKey(sessionKey);
            if(loginEvent.getLogout() != null && loginEvent.getLogout().before(Calendar.getInstance().getTime())){
                context.setGamePlayRequestErrorReason("Session : " + sessionKey + " ended : " + loginEvent.getLogout());
                throw new Status401NotLoggedInException();
            }
            return loginEvent;
        }catch (Status401NotLoggedInException exception){
            log.error("Session already logged out : " + sessionKey);
            throw exception;
        } catch (Status412LoginEventNotFoundException exception){
            log.error("Could not retrieve session for sessionKey : " + sessionKey);
            context.setGamePlayRequestErrorReason("Could not retrieve session for sessionKey : " + sessionKey);
            throw new Status401NotLoggedInException();
        } catch (Exception exception){
            log.error("Could not retrieve session info.", exception);
            context.setGamePlayRequestErrorReason("Could not retrieve session for sessionKey : " + sessionKey);
            throw new Status500InternalServerErrorException("Could not retrieve session for sessionKey : " + sessionKey);
        }
    }

    public SystemLoginEventsClient getSystemLoginEventsClient() throws LithiumServiceClientFactoryException {
        return lithiumServiceClientFactory.target(SystemLoginEventsClient.class, "service-user", true);
    }
}
