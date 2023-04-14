package lithium.service.promo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExclusiveListItem {
    private Long promotionRevisionId;
    private Long uploadId;
    private String userGuid;
}
