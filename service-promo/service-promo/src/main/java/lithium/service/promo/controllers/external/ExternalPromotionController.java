package lithium.service.promo.controllers.external;

import java.util.List;

import lithium.exceptions.Status470HashInvalidException;
import lithium.service.promo.client.exception.Status411InvalidPromotionException;
import lithium.service.promo.client.exception.Status412InvalidExclusiveUpload;
import lithium.service.promo.dtos.ExclusiveUploadRequest;
import lithium.service.promo.dtos.ExclusiveUploadProgress;
import lithium.service.promo.dtos.PromotionExt;
import lithium.service.promo.services.ExclusivePromoService;
import lithium.service.promo.services.ExternalPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/external" )
@RequiredArgsConstructor
public class ExternalPromotionController {

  private final ExternalPromoService externalPromoService;
  private final ExclusivePromoService exclusivePromoService;

  @GetMapping( "/find-promotions" )
  public List<PromotionExt> findPromotions(@RequestParam( "domainName" ) String domainName, @RequestParam( "sha256" ) String sha)
          throws Status470HashInvalidException
  {
    externalPromoService.validateSha(domainName, sha);
    return externalPromoService.getPromotionsForDomain(domainName);
  }

  @PostMapping( "/add-players" )
  public ExclusiveUploadProgress addExclusivePlayers(@RequestBody ExclusiveUploadRequest exclusiveUploadRequest)
          throws Status470HashInvalidException
  {
    externalPromoService.validateSha(exclusiveUploadRequest.getPromotionId().toString(), exclusiveUploadRequest.getSha256());
    return externalPromoService.addPlayersToPromotion(exclusiveUploadRequest.getPromotionId(), exclusiveUploadRequest.getPlayers());
  }

  @PostMapping( "/remove-players" )
  public ExclusiveUploadProgress removeExclusivePlayers(@RequestBody ExclusiveUploadRequest exclusiveRemoveRequest)
          throws Status470HashInvalidException, Status411InvalidPromotionException
  {
    externalPromoService.validateSha(exclusiveRemoveRequest.getPromotionId().toString(), exclusiveRemoveRequest.getSha256());
    return externalPromoService.removePlayersFromPromotion(exclusiveRemoveRequest.getPromotionId(), exclusiveRemoveRequest.getPlayers());
  }

  @GetMapping("/{promoExclusiveUploadId}/progress")
  public ExclusiveUploadProgress getUploadProgress(@PathVariable("promoExclusiveUploadId") Long  exclusiveUploadId, @RequestParam("sha256") String sha) {
    externalPromoService.validateSha(exclusiveUploadId.toString(), sha);
    return exclusivePromoService.getLatestUploadProgress(exclusiveUploadId)
            .orElseThrow(Status412InvalidExclusiveUpload::new);
  }
}
