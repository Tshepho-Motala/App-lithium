package lithium.service.cashier.processor.smartcash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartcashCallbackTransaction {
        private String code;
        @JsonProperty("id")
        private String transactionId;
        private String message;
        private String status_code;
        @JsonProperty("smart_cash_money_id")
        private String smartCashMoneyId;
        @JsonProperty("error_code")
        private String errorCode;
        @JsonProperty("error_msg")
        private String errorMessage;
}
