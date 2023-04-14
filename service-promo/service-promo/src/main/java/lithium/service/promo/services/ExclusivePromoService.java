package lithium.service.promo.services;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lithium.service.promo.client.enums.ExclusiveUploadStatus;
import lithium.service.promo.client.enums.ExclusiveUploadType;
import lithium.service.promo.data.entities.PromoExclusivePlayer;
import lithium.service.promo.data.entities.PromoExclusiveUpload;
import lithium.service.promo.data.entities.PromoExclusiveUploadItem;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.projections.PromoExclusiveUploadProjection;
import lithium.service.promo.data.repositories.PromoExclusiveUploadItemRepository;
import lithium.service.promo.data.repositories.PromoExclusiveUploadRepository;
import lithium.service.promo.data.repositories.PromoExclusivePlayerRepository;
import lithium.service.promo.data.repositories.PromotionRepository;
import lithium.service.promo.data.specifications.PromoExclusiveUploadItemSpecification;
import lithium.service.promo.dtos.ExclusiveListItem;
import lithium.service.promo.dtos.ExclusiveListRequest;
import lithium.service.promo.dtos.ExclusiveUploadData;
import lithium.service.promo.dtos.ExclusiveUploadProgress;
import lithium.service.promo.dtos.ProcessedExclusiveListRequest;
import lithium.service.promo.enums.PromoExclusiveUploadItemError;
import lithium.service.promo.stream.ExclusivePromoOutputQueues;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExclusivePromoService {
    private final PromoExclusivePlayerRepository promoExclusivePlayerRepository;
    private final PromoExclusiveUploadRepository promoExclusiveUploadRepository;
    private final PromoExclusiveUploadItemRepository promoExclusiveUploadItemRepository;
    private final PromotionRepository promotionRepository;
    private final UserService userService;
    private final UserApiInternalClientService userApiInternalClientService;

    private final ExclusivePromoOutputQueues exclusivePromoOutputQueues;

    public void processUpload(ExclusiveUploadData data) {
        Optional<PromoExclusiveUpload> result = promoExclusiveUploadRepository.findById(data.getPromoExclusiveUploadId());

        if (result.isEmpty()) {
            log.debug("No exclusive promo upload id {} could be found", data.getPromoExclusiveUploadId());
            return;
        }

        PromoExclusiveUpload upload = result.get();
        upload.setStatus(ExclusiveUploadStatus.UPLOADING);
        promoExclusiveUploadRepository.save(upload);

        data.getUsers().stream().map(u -> ExclusiveListItem.builder()
                        .userGuid(u.getGuid())
                        .promotionRevisionId(data.getPromotionId())
                        .uploadId(upload.getId())
                        .build())
                .forEach(this::addToUserProcessingQueue);
    }

    public ExclusiveUploadProgress upload(ExclusiveUploadData data, Promotion promotion) {

        PromoExclusiveUpload upload = createUpload(promotion, data.getUsers().size(), data.getType());
        data.setPromoExclusiveUploadId(upload.getId());
        exclusivePromoOutputQueues.exclusivePromoUpload().send(MessageBuilder.withPayload(data).build());

        return ExclusiveUploadProgress.builder()
                .promoExclusiveUploadId(upload.getId())
                .uploadStatus(upload.getStatus())
                .uploadType(upload.getType())
                .totalPlayers(upload.getTotalRecords())
                .build();
    }

    public void addToUserProcessingQueue(ExclusiveListItem exclusiveListItem) {
        exclusivePromoOutputQueues.exclusivePromoUserProcess().send(MessageBuilder.withPayload(exclusiveListItem).build());
    }

    protected PromoExclusiveUpload createUpload(Promotion promotion, Integer recordCount, ExclusiveUploadType uploadType) {
        return promoExclusiveUploadRepository.save(PromoExclusiveUpload.builder()
                .promotion(promotion)
                .totalRecords(recordCount)
                .status(ExclusiveUploadStatus.CREATED)
                .type(uploadType)
                .build());
    }

    @Transactional
    public void processExclusivePlayer(ExclusiveListItem exclusiveListItem) {

        Promotion promotion = promotionRepository.findOne(exclusiveListItem.getPromotionRevisionId());
        PromoExclusiveUpload promoExclusiveUpload = promoExclusiveUploadRepository.getById(exclusiveListItem.getUploadId());

        PromoExclusiveUploadItem uploadItem = PromoExclusiveUploadItem.builder()
                .promoExclusiveUpload(promoExclusiveUpload)
                .status(ExclusiveItemStatus.VALIDATION_IN_PROGRESS)
                .guid(exclusiveListItem.getUserGuid())
                .build();

        promoExclusiveUploadItemRepository.save(uploadItem);

        int count = promoExclusiveUploadItemRepository.countByPromoExclusiveUploadId(promoExclusiveUpload.getId());

        if (count >= promoExclusiveUpload.getTotalRecords()) {
            promoExclusiveUpload.setStatus(ExclusiveUploadStatus.DONE);
            promoExclusiveUploadRepository.save(promoExclusiveUpload);
        }

        Optional<PromoExclusivePlayer> results =  promoExclusivePlayerRepository.findOneByPlayerGuidAndPromotionId(exclusiveListItem.getUserGuid(), exclusiveListItem.getPromotionRevisionId());

        if (results.isPresent() && promoExclusiveUpload.getType().equals(ExclusiveUploadType.ADD)) {
            uploadItem.setReasonForFailure(PromoExclusiveUploadItemError.DUPLICATE_PLAYER.getError());
            uploadItem.setStatus(ExclusiveItemStatus.VALIDATION_FAILED);
            promoExclusiveUploadItemRepository.save(uploadItem);
            return;
        }

        if (promoExclusiveUpload.getType().equals(ExclusiveUploadType.REMOVE)) {
            if (results.isPresent()) {
                promoExclusivePlayerRepository.delete(results.get());
                uploadItem.setStatus(ExclusiveItemStatus.VALIDATION_SUCCESS);
            } else {
                uploadItem.setReasonForFailure(PromoExclusiveUploadItemError.PLAYER_NOT_ON_THE_LIST.getError());
                uploadItem.setStatus(ExclusiveItemStatus.VALIDATION_FAILED);
                promoExclusiveUploadItemRepository.save(uploadItem);
            }

            return;
        }

        PromoExclusivePlayer player = PromoExclusivePlayer.builder()
                .promotion(promotion)
                .build();

        User localUser = userService.find(exclusiveListItem.getUserGuid());

        if (Objects.isNull(localUser)) {
            try {
                var user=  userApiInternalClientService.getUserByGuid(exclusiveListItem.getUserGuid());
                localUser = userService.findOrCreate(exclusiveListItem.getUserGuid());
                localUser.setTimezone(user.getTimezone());
                localUser.setTestAccount(user.getTestAccount());
            } catch (Throwable e) {

                String promotionName = Optional.ofNullable(promotion.getCurrent()).map(PromotionRevision::getName).orElse(null);
                promotionName = Objects.isNull(promotionName) ?  Optional.ofNullable(promotion.getEdit()).map(PromotionRevision::getName).orElse(null) : promotionName;

                uploadItem.setStatus(ExclusiveItemStatus.VALIDATION_FAILED);
                uploadItem.setReasonForFailure(e.getMessage());
                promoExclusiveUploadItemRepository.save(uploadItem);
                log.error("User {} failed validation on promotion {}", exclusiveListItem.getUserGuid(), promotionName, e);
                return;
            }
        }

        player.setPlayer(localUser);
        uploadItem.setStatus(ExclusiveItemStatus.VALIDATION_SUCCESS);

        promoExclusiveUploadItemRepository.save(uploadItem);
        userService.userRepository.save(localUser);
        promoExclusivePlayerRepository.save(player);
    }

    public Page<PromoExclusiveUploadItem> getProcessedExclusiveList(ProcessedExclusiveListRequest request) {
        int size = Optional.ofNullable(request.getSize()).orElse(50);
        int page = Math.max(0,  Optional.ofNullable(request.getPage()).orElse(0) - 1); //jpa pagination starts from zero(0)

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Optional<ExclusiveItemStatus> optionalStatus = Optional.ofNullable(request.getStatus());
        Specification<PromoExclusiveUploadItem> specification = PromoExclusiveUploadItemSpecification.forPromotion(request.getPromotionId());

        if (optionalStatus.isPresent()) {
            specification = specification.and(PromoExclusiveUploadItemSpecification.withStatus(optionalStatus.get()));
        }

        return promoExclusiveUploadItemRepository.findAll(specification, pageable);
    }

    public Page<PromoExclusivePlayer> getExclusiveList(ExclusiveListRequest request) {
        int size = Optional.ofNullable(request.getSize()).orElse(50);
        int page = Math.max(0,  Optional.ofNullable(request.getPage()).orElse(0) - 1); //jpa pagination starts from zero(0)
        Pageable pageable = PageRequest.of(page, size);

        return promoExclusivePlayerRepository.findAllByPromotionId(request.getPromotionId(), pageable);
    }

    public Optional<ExclusiveUploadProgress> getLatestUploadProgressForRevision(Long promoId) {
        Optional<PromoExclusiveUploadProjection> projection = promoExclusiveUploadRepository.findFirstByPromotionIdOrderByIdDesc(promoId);
        return mapToProgress(projection);
    }

    public Optional<ExclusiveUploadProgress> getLatestUploadProgress(Long uploadId) {
        Optional<PromoExclusiveUploadProjection> projection = promoExclusiveUploadRepository.findOneById(uploadId);
        return mapToProgress(projection);
    }

    public  Optional<ExclusiveUploadProgress> mapToProgress(Optional<PromoExclusiveUploadProjection> projection) {
        return projection.map(p -> {
            int failedCount = promoExclusiveUploadItemRepository.countByPromoExclusiveUploadIdAndStatus(p.getId(), ExclusiveItemStatus.VALIDATION_FAILED);
            int validCount = promoExclusiveUploadItemRepository.countByPromoExclusiveUploadIdAndStatus(p.getId(), ExclusiveItemStatus.VALIDATION_SUCCESS);
            int processed = promoExclusiveUploadItemRepository.countByPromoExclusiveUploadId(p.getId());
            return ExclusiveUploadProgress.builder()
                    .promoExclusiveUploadId(p.getId())
                    .uploadStatus(p.getStatus())
                    .invalidPlayers(failedCount)
                    .validPlayers(validCount)
                    .processedPlayers(processed)
                    .uploadType(p.getType())
                    .totalPlayers(p.getTotalRecords())
                    .build();
        });
    }
}
