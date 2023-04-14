package lithium.service.promo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExclusiveListRequest {
    private Long promotionId;
    private Integer size;
    private Integer page;

}
