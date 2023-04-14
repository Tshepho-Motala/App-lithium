package lithium.service.casino.provider.evolution.service.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lithium.service.casino.provider.evolution.auth")
@Data
public class EvolutionConfiguration {
    private int timeoutInMilliseconds;
}
