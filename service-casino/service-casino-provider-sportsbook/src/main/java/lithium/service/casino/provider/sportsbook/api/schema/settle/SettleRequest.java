package lithium.service.casino.provider.sportsbook.api.schema.settle;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    String betId;
    Double amount;
}
