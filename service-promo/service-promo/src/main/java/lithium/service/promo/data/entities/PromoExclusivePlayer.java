package lithium.service.promo.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promo_exclusive_players",
indexes = {
        @Index(name = "idx_player_id_promotion_id_unique", columnList = "player_id, promotion_id", unique = true)
})
public class PromoExclusivePlayer implements Serializable {
    @Serial
    private static final long serialVersionUID = 8847690772069657701L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private User player;

    private LocalDateTime lastAdded;

    @PrePersist
    void prePersist() {
        lastAdded = Optional.ofNullable(lastAdded).orElse(LocalDateTime.now());
    }
}
