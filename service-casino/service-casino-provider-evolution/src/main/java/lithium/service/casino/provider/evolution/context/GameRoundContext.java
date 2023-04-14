package lithium.service.casino.provider.evolution.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameRoundContext {
    private String domainName;
    private BigDecimal amount = BigDecimal.ZERO;
    private String gameRoundTransactionId;
    private Long loginEventId;
    private String gameGuid;
    private String gamePlayRequestErrorReason;
}
