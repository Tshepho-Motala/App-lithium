package lithium.service.kyc.provider.onfido.exceptions;

import lombok.Getter;

public abstract class CodeException extends Exception {
    @Getter
    private final int code;

    protected CodeException(int code, String message) {
        super(message);
        this.code = code;
    }
}
