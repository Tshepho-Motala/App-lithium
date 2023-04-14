package lithium.service.casino.provider.evolution.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceRequest {

    private String userId;

    private String sid;

    private String uuid;

    private String currency;

    private Game game;

}
