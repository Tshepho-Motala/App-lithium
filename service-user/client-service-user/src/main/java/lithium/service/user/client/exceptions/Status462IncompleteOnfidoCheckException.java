package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status462IncompleteOnfidoCheckException extends NotRetryableErrorCodeException {
    public Status462IncompleteOnfidoCheckException(String message) {
        super(462, message, Status462IncompleteOnfidoCheckException.class.getCanonicalName());
    }
}
