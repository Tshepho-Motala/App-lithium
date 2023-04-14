package lithium.service.promo.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ExclusivePromoQueuesSink {
    String UPLOAD_INPUT = "exclusive-promo-upload-input";
    String PROCESS_USER_INPUT = "exclusive-promo-user-process-input";

    @Input(UPLOAD_INPUT)
    SubscribableChannel inputUploadChannel();

    @Input(PROCESS_USER_INPUT)
    SubscribableChannel inputUserProcessChannel();

}
