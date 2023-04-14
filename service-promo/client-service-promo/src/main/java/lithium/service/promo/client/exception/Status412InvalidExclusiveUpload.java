package lithium.service.promo.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

import java.io.Serial;

public class Status412InvalidExclusiveUpload extends NotRetryableErrorCodeException {
    @Serial
    private static final long serialVersionUID = 2643613621373675976L;

    public static final int CODE = 412;

    public Status412InvalidExclusiveUpload() {
        super(CODE, "An invalid exclusive upload id was provided", Status411InvalidPromotionException.class.getCanonicalName());
    }
}
