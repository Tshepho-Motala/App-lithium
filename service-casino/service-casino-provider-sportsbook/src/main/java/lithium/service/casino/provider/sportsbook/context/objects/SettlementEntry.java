package lithium.service.casino.provider.sportsbook.context.objects;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementEntry implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  private Bet bet;
  private Double amount;
  private int type;
  private int requestIndex; //The iteration index when the settlement list was received
  private long accountingTransactionId;
}
