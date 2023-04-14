package lithium.service.reward.provider.casino.pragmatic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PragmaticGame {

    private String gameId;

    @Builder.Default
    private List<VariableBetValue> betValues = new ArrayList<>();
}
