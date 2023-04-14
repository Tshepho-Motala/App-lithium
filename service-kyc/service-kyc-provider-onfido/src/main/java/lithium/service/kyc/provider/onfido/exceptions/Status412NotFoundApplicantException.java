package lithium.service.kyc.provider.onfido.exceptions;


public class Status412NotFoundApplicantException extends CodeException {
    public Status412NotFoundApplicantException(String message) {
        super(412, message);
    }
}
