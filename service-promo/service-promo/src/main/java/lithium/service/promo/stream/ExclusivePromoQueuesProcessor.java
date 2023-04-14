package lithium.service.promo.stream;


import lithium.service.promo.dtos.ExclusiveListItem;
import lithium.service.promo.dtos.ExclusiveUploadData;
import lithium.service.promo.services.ExclusivePromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@EnableBinding(ExclusivePromoQueuesSink.class)
@Component
@Slf4j
public class ExclusivePromoQueuesProcessor {

    private final ExclusivePromoService exclusivePromoService;

    @StreamListener(ExclusivePromoQueuesSink.UPLOAD_INPUT)
    public void promoExclusiveUpload(ExclusiveUploadData data) {
        exclusivePromoService.processUpload(data);
    }


    @StreamListener(ExclusivePromoQueuesSink.PROCESS_USER_INPUT)
    public void promoExclusiveUserProcess(ExclusiveListItem exclusiveListItem) {
        log.debug("Received exclusive user to validate, {}", exclusiveListItem);
        exclusivePromoService.processExclusivePlayer(exclusiveListItem);
    }
}
