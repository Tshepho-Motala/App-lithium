package lithium.service.promo.converter;

import lithium.service.promo.client.enums.ExclusiveUploadStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExclusiveUploadStatusConverter  implements AttributeConverter<ExclusiveUploadStatus, String> {
    @Override
    public String convertToDatabaseColumn(ExclusiveUploadStatus exclusiveUploadStatus) {
        return exclusiveUploadStatus.getStatus();
    }

    @Override
    public ExclusiveUploadStatus convertToEntityAttribute(String s) {
        return ExclusiveUploadStatus.fromStatus(s);
    }
}
