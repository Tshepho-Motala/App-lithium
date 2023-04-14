package lithium.service.casino.provider.evolution.exception;


public class Status10003InvalidSIDException extends RuntimeException {
    public Status10003InvalidSIDException(String message) {
        super(message);
    }

    public Status10003InvalidSIDException(String message, Throwable exception) {
        super(message, exception);
    }
}
