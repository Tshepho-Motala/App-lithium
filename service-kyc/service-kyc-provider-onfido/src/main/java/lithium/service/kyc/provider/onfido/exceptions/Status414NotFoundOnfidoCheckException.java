package lithium.service.kyc.provider.onfido.exceptions;

public class Status414NotFoundOnfidoCheckException extends CodeException {
    public Status414NotFoundOnfidoCheckException(String message) {
        super(414, message);
    }
}
