package lithium.service.promo.data.entities;

import lithium.service.promo.client.enums.ExclusiveUploadStatus;
import lithium.service.promo.client.enums.ExclusiveUploadType;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
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
public class PromoExclusiveUpload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1628645800913279242L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private ExclusiveUploadStatus status;
    private ExclusiveUploadType type;
    private Integer totalRecords;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @PrePersist()
    void prePersist() {
        createdAt = Optional.ofNullable(createdAt).orElse(LocalDateTime.now());
    }

}
