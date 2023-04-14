package lithium.service.reward.provider.casino.pragmatic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PragmaticRewardResponse {
    private String error;
    private String description;
}
