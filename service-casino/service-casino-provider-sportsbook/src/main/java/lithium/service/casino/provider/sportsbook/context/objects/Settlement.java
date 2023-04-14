package lithium.service.casino.provider.sportsbook.context.objects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settlement implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  private long createdDate;
  private long modifiedDate;
  private Date timestamp;
  private long requestId;
  private Currency currency;
  private Double balanceAfter;
  private List<SettlementEntry> settlementEntries;
}
