package lithium.service.promo.dtos;

import lithium.service.promo.client.enums.ExclusiveUploadStatus;
import lithium.service.promo.client.enums.ExclusiveUploadType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExclusiveUploadProgress {
    private int totalPlayers;
    private int processedPlayers;
    private int invalidPlayers;
    private int validPlayers;

    private Long promoExclusiveUploadId;
    private ExclusiveUploadType uploadType;
    private ExclusiveUploadStatus uploadStatus;
}
