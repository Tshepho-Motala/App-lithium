package lithium.service.promo.services;

import lithium.exceptions.Status470HashInvalidException;
import lithium.service.promo.client.enums.ExclusiveUploadType;
import lithium.service.promo.client.exception.Status411InvalidPromotionException;
import lithium.service.promo.config.ServicePromoConfigurationProperties;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.repositories.PromotionRepository;

import lithium.service.promo.dtos.ExclusiveUploadData;
import lithium.service.promo.dtos.ExclusiveUploadProgress;
import lithium.service.promo.dtos.PromotionExt;
import lithium.util.HmacSha256HashCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPromoService {

    private final PromotionRepository promotionRepository;
    private final ServicePromoConfigurationProperties configurationProperties;
    private final ExclusivePromoService exclusivePromoService;

    public List<PromotionExt> getPromotionsForDomain(String domainName) {
        List<Promotion> promotionList =  promotionRepository.findByCurrentDomainNameAndEnabledIsTrue(domainName);
        return promotionList.stream().map(promotion -> convertToPromotionExt(promotion))
                .toList();
    }

    public ExclusiveUploadProgress addPlayersToPromotion(Long promotionId, List<String> playerGuids) {
        Promotion promotion = findPromotion(promotionId);

        return exclusivePromoService.upload(
                ExclusiveUploadData.builder()
                        .type(ExclusiveUploadType.ADD)
                        .promotionId(promotionId)
                        .users(playerGuids.stream().map(playerGuid ->
                                        lithium.service.promo.client.objects.User.builder().guid(playerGuid).build())
                                .collect(Collectors.toSet()))
                        .build(), promotion);
    }

    public ExclusiveUploadProgress removePlayersFromPromotion(Long revisionId, List<String> playerGuids)
            throws Status411InvalidPromotionException, Status470HashInvalidException {
        Promotion promotion = findPromotion(revisionId);

        return exclusivePromoService.upload(
                ExclusiveUploadData.builder()
                        .type(ExclusiveUploadType.REMOVE)
                        .promotionId(revisionId)
                        .users(playerGuids.stream().map(playerGuid ->
                                        lithium.service.promo.client.objects.User.builder().guid(playerGuid).build())
                                .collect(Collectors.toSet()))
                        .build(), promotion);
    }

    public void validateSha(String key, String sha) throws Status470HashInvalidException {
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(configurationProperties.getExternalSecretKey());
        hasher.addItem(key);
        hasher.validate(sha, log, key);
    }

    private PromotionExt convertToPromotionExt(Promotion promotion) {
        return PromotionExt.builder()
                .id(promotion.getId())
                .name(promotion.getCurrent().getName())
                .description(promotion.getCurrent().getDescription())
                .build();
    }

    private Promotion findPromotion(Long revisionId) throws Status411InvalidPromotionException{
        Promotion promotion = promotionRepository.findOne(revisionId);
        if (promotion == null) {
            throw new Status411InvalidPromotionException();
        }

        return promotion;
    }
}
