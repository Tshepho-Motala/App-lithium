package lithium.service.kyc.provider.onfido.exceptions;

public class Status411FailOnfidoServiceException extends CodeException {
    public Status411FailOnfidoServiceException(String message) {
        super(411, message);
    }
}
