package lithium.service.promo.converter;

import lithium.service.promo.client.enums.ExclusiveItemStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExclusiveItemUploadStatusConverter implements AttributeConverter<ExclusiveItemStatus, String> {
    @Override
    public String convertToDatabaseColumn(ExclusiveItemStatus exclusiveItemStatus) {
        return exclusiveItemStatus.getStatus();
    }

    @Override
    public ExclusiveItemStatus convertToEntityAttribute(String s) {
        return ExclusiveItemStatus.fromStatus(s);
    }
}
