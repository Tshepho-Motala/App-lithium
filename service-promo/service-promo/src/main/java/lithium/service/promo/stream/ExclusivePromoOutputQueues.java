package lithium.service.promo.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ExclusivePromoOutputQueues {
    @Output("exclusive-promo-upload-output")
    MessageChannel exclusivePromoUpload();

    @Output("exclusive-promo-user-process-output")
    MessageChannel exclusivePromoUserProcess();
}
