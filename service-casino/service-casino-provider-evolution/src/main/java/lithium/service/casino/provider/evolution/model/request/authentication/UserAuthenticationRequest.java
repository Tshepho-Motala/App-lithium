package lithium.service.casino.provider.evolution.model.request.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAuthenticationRequest {
    private String uuid;
    private Player player;
    private GameConfiguration config;
}
