package lithium.service.casino.provider.evolution.service;

import lithium.service.casino.provider.evolution.exception.Status10003InvalidTokenIdException;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@AllArgsConstructor
public class SecurityConfigUtilsUTest extends BalanceMockService{

    @ParameterizedTest
    @DisplayName("Should pass an auth token of format A-Z, a-z, 0-9, -, ., _ and ~.")
    @ValueSource(strings = {"s3cr-tV4_u3", "s3cr3tV4lu3", "s3cr3tV-_u3", "A9cd3tV4_u3", "A9_d~tV4_u3"})
    void givenValidTokens_when_validateSecurity_Then_Succeed(String authToken) {
        mockExternalServices();
        validProviderConfigurations(authToken);
        Assertions.assertDoesNotThrow(() -> securityConfigUtils.validateAuthToken(authToken, "livescore_uk"));
    }

    @ParameterizedTest
    @DisplayName("Should pass an auth token of format A-Z, a-z, 0-9, -, ., _ and ~.")
    @ValueSource(strings = {"s3cr-tV4_u3zv", "s3cr3thjsV4lu3", "s3cr3tV-gtsv_u3", "A9cd3tV4_hgasdu3", "A9_d~jhgsdvshjtV4_u3"})
    void givenNonExistentTokens_when_validateSecurity_Then_Status10003InvalidTokenIdException(String authToken) {
        mockExternalServices();
        validProviderConfigurations(authToken);
        Assertions.assertThrows(Status10003InvalidTokenIdException.class,() -> securityConfigUtils.validateAuthToken(authToken, "livescore_uk"));
    }

    @ParameterizedTest
    @DisplayName("Should pass an auth token of format A-Z, a-z, 0-9, -, ., _ and ~.")
    @ValueSource(strings = {"s3cr-tV4_*%==u3", "s3cr%$^3tV4lu3", "s3cr3*&^%tV-_u3", "A9cd3tV4_#$@u3", "A9_%^$$@^d~tV4_u3"})
    void givenInvalidTokenValues_when_validateSecurity_Then_Status10003InvalidTokenIdException(String authToken) {
        mockExternalServices();
        validProviderInvalidTokensConfigurations(authToken);
        Assertions.assertThrows(Status10003InvalidTokenIdException.class,() -> securityConfigUtils.validateAuthToken(authToken, "livescore_uk"));
    }
}
