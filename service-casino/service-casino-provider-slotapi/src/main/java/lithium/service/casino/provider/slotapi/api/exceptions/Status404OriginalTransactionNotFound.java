package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404OriginalTransactionNotFound extends NotRetryableErrorCodeException {

    public Status404OriginalTransactionNotFound() {
        super(404, "Original transaction not found", Status404OriginalTransactionNotFound.class.getCanonicalName());
    }

}
