package lithium.service.promo.data.repositories;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lithium.service.promo.data.entities.PromoExclusiveUploadItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoExclusiveUploadItemRepository extends JpaRepository<PromoExclusiveUploadItem, Long>, JpaSpecificationExecutor<PromoExclusiveUploadItem> {
    int countByPromoExclusiveUploadIdAndStatus(Long promoExclusiveUploadId, ExclusiveItemStatus status);
    int countByPromoExclusiveUploadId(Long promoExclusiveUploadId);
}

