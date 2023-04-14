package lithium.service.casino.provider.evolution.configuration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderConfiguration {

    private String authToken;
    private String startGameUrl;
    private String evolutionAuthUrl;
}
