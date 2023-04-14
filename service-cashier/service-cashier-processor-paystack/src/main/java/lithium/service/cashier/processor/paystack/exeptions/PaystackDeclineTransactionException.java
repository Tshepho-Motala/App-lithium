package lithium.service.cashier.processor.paystack.exeptions;

import lombok.Data;

@Data
public class PaystackDeclineTransactionException extends Exception {

    public PaystackDeclineTransactionException(String message) {
        super(message);
    }
}
