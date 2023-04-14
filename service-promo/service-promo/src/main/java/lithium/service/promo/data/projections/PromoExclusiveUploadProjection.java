package lithium.service.promo.data.projections;

import lithium.service.promo.client.enums.ExclusiveUploadStatus;
import lithium.service.promo.client.enums.ExclusiveUploadType;

public interface PromoExclusiveUploadProjection {
    Long getId();
    ExclusiveUploadStatus getStatus();
    ExclusiveUploadType getType();
    int getTotalRecords();
}
