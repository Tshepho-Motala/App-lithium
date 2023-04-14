package lithium.service.reward.provider.casino.pragmatic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VariableBetValue {

  private BigDecimal betPerLine;

  private String currency;
}
