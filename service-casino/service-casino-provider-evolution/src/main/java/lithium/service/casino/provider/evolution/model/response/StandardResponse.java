package lithium.service.casino.provider.evolution.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponse {

    private String sid;

    private String uuid;

    private String status;

    private BigDecimal balance;

    private BigDecimal bonus;

    private Boolean retransmission;

}
