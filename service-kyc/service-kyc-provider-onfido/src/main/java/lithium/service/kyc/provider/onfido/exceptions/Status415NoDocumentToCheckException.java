package lithium.service.kyc.provider.onfido.exceptions;

public class Status415NoDocumentToCheckException extends CodeException {
    public Status415NoDocumentToCheckException(String message) {
        super(415, message);
    }
}
