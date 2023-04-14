package lithium.exceptions;

public class Status400BadRequestException extends NotRetryableErrorCodeException {
    public Status400BadRequestException(String message) {
        super(400, message, Status400BadRequestException.class.getCanonicalName());
    }
}
