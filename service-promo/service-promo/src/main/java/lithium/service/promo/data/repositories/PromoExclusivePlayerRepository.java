package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromoExclusivePlayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoExclusivePlayerRepository extends JpaRepository<PromoExclusivePlayer, Long> {
    Optional<PromoExclusivePlayer> findOneByPlayerGuidAndPromotionId(String playerGuid, Long promotionId);
    Page<PromoExclusivePlayer> findAllByPromotionId(Long promotionId, Pageable pageable);
}
