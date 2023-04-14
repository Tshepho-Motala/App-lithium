package lithium.service.casino.provider.evolution.util;

import lithium.service.casino.provider.evolution.configuration.ProviderConfiguration;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationService;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidTokenIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityConfigUtils {

    private final ProviderConfigurationService providerConfigurationService;

    public void validateAuthToken(String authorization, String domainName) {
        ProviderConfiguration providerConfiguration = providerConfigurationService.getEvolutionProviderConfig(domainName);
        validateAuthentication(authorization, providerConfiguration);
    }

    private void validateAuthentication(String authorization, ProviderConfiguration providerConfiguration) {
        if(!validateAthToken(authorization)){
            throw new Status10003InvalidTokenIdException("Invalid token");
        }
        if(!providerConfiguration.getAuthToken().equals(authorization)){
            throw new Status10003InvalidTokenIdException("Invalid token");
        }
    }

    private Boolean validateAthToken(String authorization) {
        if(authorization != null){
            if(authorization.length() > 8 && authorization.length() < 23){
                Pattern pattern = Pattern.compile("^[\\w_.~-]*$");
                Matcher matcher = pattern.matcher(authorization);
                return matcher.find();
            }else {
                return false;
            }
        }
        return false;
    }
}
