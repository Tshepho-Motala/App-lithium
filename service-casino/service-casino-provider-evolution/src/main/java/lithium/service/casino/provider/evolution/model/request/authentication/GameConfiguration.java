package lithium.service.casino.provider.evolution.model.request.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameConfiguration {
    private Channel channel;
    private PlayerGame game;
}
