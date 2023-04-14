package lithium.service.promo.data.specifications;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lithium.service.promo.data.entities.PromoExclusiveUpload;
import lithium.service.promo.data.entities.PromoExclusiveUploadItem;
import lithium.service.promo.data.entities.PromoExclusiveUploadItem_;
import lithium.service.promo.data.entities.PromoExclusiveUpload_;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.PromotionRevision_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class PromoExclusiveUploadItemSpecification {

    public static Specification<PromoExclusiveUploadItem> forPromotion(Long promotionId) {
        return (root, query, cb) -> {
            Join<PromoExclusiveUploadItem, PromoExclusiveUpload> uploadJoin = root.join(PromoExclusiveUploadItem_.promoExclusiveUpload, JoinType.INNER);
            Join<PromoExclusiveUpload, Promotion> promotionRevisionJoin = uploadJoin.join(PromoExclusiveUpload_.promotion, JoinType.INNER);
            return  cb.equal(promotionRevisionJoin.get(PromotionRevision_.ID), promotionId);
        };
    }

    public static Specification<PromoExclusiveUploadItem> withStatus(ExclusiveItemStatus status) {
        return (root, query, cb) -> cb.equal(root.get(PromoExclusiveUploadItem_.status), status);
    }
}
