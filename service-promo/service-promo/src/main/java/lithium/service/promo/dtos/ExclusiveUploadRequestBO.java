package lithium.service.promo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExclusiveUploadRequestBO {
    private Long promotionId;
    @Builder.Default
    private Set<String> players = new HashSet<>();
}
