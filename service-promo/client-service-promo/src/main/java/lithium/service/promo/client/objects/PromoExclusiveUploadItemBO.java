package lithium.service.promo.client.objects;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromoExclusiveUploadItemBO {
    @Serial
    private static final long serialVersionUID = 1628645800913279242L;

    private long id;
    private ExclusiveItemStatus status;
    private String guid;
    private String reasonForFailure;
}
