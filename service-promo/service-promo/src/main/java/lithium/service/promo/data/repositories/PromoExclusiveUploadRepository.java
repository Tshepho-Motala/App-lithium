package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromoExclusiveUpload;
import lithium.service.promo.data.projections.PromoExclusiveUploadProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoExclusiveUploadRepository extends JpaRepository<PromoExclusiveUpload, Long> {
    Optional<PromoExclusiveUploadProjection> findFirstByPromotionIdOrderByIdDesc(Long promoId);
    Optional<PromoExclusiveUploadProjection> findOneById(Long uploadId);
}
