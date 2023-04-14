package lithium.service.promo.dtos;

import lithium.service.promo.client.enums.ExclusiveUploadType;
import lithium.service.promo.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExclusiveUploadData {
    private Long promotionId;
    private Long promoExclusiveUploadId;
    private Set<User> users;
    private ExclusiveUploadType type;
}
