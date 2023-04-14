package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status463CantUpdateBiometricStatusException extends NotRetryableErrorCodeException {
    public Status463CantUpdateBiometricStatusException(String message) {
        super(463, message, Status463CantUpdateBiometricStatusException.class.getCanonicalName());
    }
}
