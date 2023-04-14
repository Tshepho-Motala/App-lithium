package lithium.service.kyc.provider.onfido.exceptions;


public class Status400DisabledOnfidoReportException extends CodeException {
    public Status400DisabledOnfidoReportException(String message) {
        super(400, message);
    }
}
