package lithium.service.promo.controllers.backoffice;

import lithium.service.Response;
import lithium.service.promo.client.enums.ExclusiveUploadType;
import lithium.service.promo.client.objects.PromoExclusiveUploadItemBO;
import lithium.service.promo.client.objects.User;
import lithium.service.promo.dtos.ExclusiveListRequest;
import lithium.service.promo.dtos.ExclusiveUploadProgress;
import lithium.service.promo.dtos.ExclusiveUploadRequestBO;
import lithium.service.promo.dtos.ProcessedExclusiveListRequest;
import lithium.service.promo.services.ExclusivePromoService;
import lithium.service.promo.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/backoffice/exclusive-promotion/v1")
public class BackofficeExclusivePromoController {
    private final ExclusivePromoService exclusivePromoService;
    private final PromotionService promotionService;

    @PostMapping("/processed-exclusive-list")
    public Page<PromoExclusiveUploadItemBO> getExclusiveList(@RequestBody ProcessedExclusiveListRequest request) {
        return exclusivePromoService.getProcessedExclusiveList(request)
                .map(item -> PromoExclusiveUploadItemBO.builder()
                        .status(item.getStatus())
                        .reasonForFailure(item.getReasonForFailure())
                        .id(item.getId())
                        .guid(item.getGuid())
                        .build());
    }


    @PostMapping("/exclusive-list")
    public Page<User> getExclusiveList(@RequestBody ExclusiveListRequest request) {
        return  exclusivePromoService.getExclusiveList(request)
                .map(item -> User.builder()
                        .guid(item.getPlayer().guid())
                        .id(item.getPlayer().getId())
                        .build());
    }

    @PostMapping("/add")
    public Response<ExclusiveUploadProgress> add(@RequestBody ExclusiveUploadRequestBO request) {

        ExclusiveUploadProgress result = promotionService.uploadExclusivePlayers(request.getPlayers(), request.getPromotionId(), ExclusiveUploadType.ADD);

        return Response.<ExclusiveUploadProgress>builder()
                .status(Response.Status.OK_SUCCESS)
                .data(result)
                .build();
    }

    @PostMapping("/remove")
    public Response<ExclusiveUploadProgress> remove(@RequestBody ExclusiveUploadRequestBO request) {
        ExclusiveUploadProgress result = promotionService.uploadExclusivePlayers(request.getPlayers(), request.getPromotionId(), ExclusiveUploadType.REMOVE);

        return Response.<ExclusiveUploadProgress>builder()
                .status(Response.Status.OK_SUCCESS)
                .data(result)
                .build();
    }

    @GetMapping("/{promotionRevisionId}/latest-upload-progress")
    public Response<ExclusiveUploadProgress> getLatestUpload(@PathVariable(name = "promotionRevisionId") Long promotionRevisionId) {
        Optional<ExclusiveUploadProgress> result = exclusivePromoService.getLatestUploadProgressForRevision(promotionRevisionId);

        return result.map(p -> Response.<ExclusiveUploadProgress>builder()
                        .status(Response.Status.OK_SUCCESS)
                        .data(p)
                        .build())
                .orElse(Response.<ExclusiveUploadProgress>builder().
                        status(Response.Status.NOT_FOUND)
                        .build());
    }

    @GetMapping("/{promoExclusiveUploadId}/progress")
    public Response<ExclusiveUploadProgress> getUploadProgress(@PathVariable(name = "promoExclusiveUploadId") Long promoExclusiveUploadId) {
        Optional<ExclusiveUploadProgress> result = exclusivePromoService.getLatestUploadProgress(promoExclusiveUploadId);

        return result.map(p -> Response.<ExclusiveUploadProgress>builder()
                        .status(Response.Status.OK_SUCCESS)
                        .data(p)
                        .build())
                .orElse(Response.<ExclusiveUploadProgress>builder().
                        status(Response.Status.NOT_FOUND)
                        .build());
    }
}
