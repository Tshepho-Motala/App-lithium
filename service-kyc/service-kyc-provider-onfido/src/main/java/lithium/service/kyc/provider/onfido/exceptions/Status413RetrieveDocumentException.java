package lithium.service.kyc.provider.onfido.exceptions;


public class Status413RetrieveDocumentException extends CodeException {
    public Status413RetrieveDocumentException(String message) {
        super(413, message);
    }
}
