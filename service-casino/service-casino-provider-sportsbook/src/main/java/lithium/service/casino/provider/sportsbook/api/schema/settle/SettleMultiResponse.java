package lithium.service.casino.provider.sportsbook.api.schema.settle;

import java.io.Serializable;
import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleMultiResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    CurrencyAmount balance;
    String balanceCurrencyCode;
    Long transactionId;
}
