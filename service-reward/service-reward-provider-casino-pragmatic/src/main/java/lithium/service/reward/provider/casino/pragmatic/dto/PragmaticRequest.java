package lithium.service.reward.provider.casino.pragmatic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PragmaticRequest {
    private List<PragmaticGame> gameList;
}
