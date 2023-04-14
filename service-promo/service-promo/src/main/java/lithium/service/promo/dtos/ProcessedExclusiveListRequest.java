package lithium.service.promo.dtos;

import lithium.service.promo.client.enums.ExclusiveItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedExclusiveListRequest  extends ExclusiveListRequest {
    private ExclusiveItemStatus status;
}
