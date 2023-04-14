package lithium.service.promo.data.entities;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
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
public class PromoExclusiveUploadItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1628645800913279242L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private ExclusiveItemStatus status;
    private String guid;
    private String reasonForFailure;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "promo_exclusive_upload_id")
    private PromoExclusiveUpload promoExclusiveUpload;

    @PrePersist()
    void prePersist() {
        createdAt = Optional.ofNullable(createdAt).orElse(LocalDateTime.now());
    }

}
