package lithium.service.casino.provider.sportsbook.api.schema.settle;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleMultiRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    String guid;
    Long requestId;
    Long timestamp;
    String sha256;
    ArrayList<SettleRequest> settleRequests;
}
