package lithium.service.casino.provider.sportsbook.context.objects;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  private long createdDate;
  private long modifiedDate;
  private User user;
  private long reserveId;
  private Double amount;
  private Date timestamp;
  private Double balanceAfter;
  private Currency currency;
  private Long accountingTransactionId;
  private Long sessionId;
  private Double bonusUsedAmount;
  private Double totalBetAmount;
  private ReservationStatus reservationStatus;
  private Date accountingLastRechecked;
  private ReservationCancel reservationCancel;
  private ReservationCommit reservationCommit;
  private Long betCount;
}
