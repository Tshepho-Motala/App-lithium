package lithium.service.casino.provider.sportsbook.context.objects;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bet implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  private long createdDate;
  private long modifiedDate;
  private User user;
  private String betId;
  private String purchaseId;
  private Reservation reservation;
  private Long requestId;
  private Double amount;
  private Date timestamp;
  private Double balanceAfter;
  private Long accountingTransactionId;
}
