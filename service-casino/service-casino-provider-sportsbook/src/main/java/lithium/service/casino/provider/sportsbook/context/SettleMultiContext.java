package lithium.service.casino.provider.sportsbook.context;

import java.io.Serializable;
import lithium.metrics.builders.sportsbook.SportsbookBetType;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiResponse;
import lithium.service.casino.provider.sportsbook.context.objects.Bet;
import lithium.service.casino.provider.sportsbook.context.objects.Currency;
import lithium.service.casino.provider.sportsbook.context.objects.Domain;
import lithium.service.casino.provider.sportsbook.context.objects.User;
import lithium.service.casino.provider.sportsbook.context.objects.Settlement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashMap;
import java.util.Locale;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettleMultiContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Locale locale;
    private User user;
    private Domain domain;
    private HashMap<String, Bet> betMap;
    private Currency currency;
    private Settlement settlement;
    private SettleMultiRequest request;
    private SettleMultiResponse response;
    private String convertedGuid;

    public String domainName() {
        if (this.domain != null) {
            return this.getDomain().getName();
        }

        if (this.user != null && this.user.getDomain() != null) {
            return this.user.getDomain().getName();
        }

        if (this.request != null && this.request.getGuid().contains("/")) {
            return this.request.getGuid().split("/")[0];
        } else {
            // This should never be the case
            return "unknown";
        }
    }

    public boolean hasWin() {
        if (this.request.getSettleRequests().stream().map(settleRequest -> settleRequest.getAmount() > 0)
            .findAny().get()) {
            return true;
        }
        return false;
    }

    public boolean hasResettlement() {
        if (this.request.getSettleRequests().stream().map(settleRequest -> settleRequest.getAmount() < 0)
            .findAny().get()) {
            return true;
        }
        return false;
    }

    public SportsbookBetType sportsbookBetType() {
        if (hasWin()) {
            return SportsbookBetType.WIN;
        }
        if (hasResettlement()) {
            return SportsbookBetType.RESETTLE;
        }
        return SportsbookBetType.LOSS;
    }
}
