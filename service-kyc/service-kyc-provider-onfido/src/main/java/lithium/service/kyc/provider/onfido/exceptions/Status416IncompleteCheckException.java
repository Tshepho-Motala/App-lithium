package lithium.service.kyc.provider.onfido.exceptions;

public class Status416IncompleteCheckException extends CodeException {
    public Status416IncompleteCheckException(String message) {
        super(416, message);
    }
}
