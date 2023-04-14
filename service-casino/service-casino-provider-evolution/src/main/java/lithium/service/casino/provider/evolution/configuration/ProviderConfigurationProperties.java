package lithium.service.casino.provider.evolution.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderConfigurationProperties {
    AUTH_TOKEN("authToken", "Security auth token for Evolution API."),
    STARTGAME_BASE_URL("startGameUrl", "The operator will be provided with a BaseUrl to launch any Desktop or Mobile game."),
    EVOLUTION_AUTH_ENDPOINT("evolutionAuthEndpoint", "Evolution endpoint for authentication");

    private final String name;
    private final String tooltip;
}
