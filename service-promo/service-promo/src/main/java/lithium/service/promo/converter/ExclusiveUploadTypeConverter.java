package lithium.service.promo.converter;

import lithium.service.promo.client.enums.ExclusiveUploadType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExclusiveUploadTypeConverter implements AttributeConverter<ExclusiveUploadType, String> {
    @Override
    public String convertToDatabaseColumn(ExclusiveUploadType exclusiveUploadtype) {
        return exclusiveUploadtype.getType();
    }

    @Override
    public ExclusiveUploadType convertToEntityAttribute(String s) {
        return ExclusiveUploadType.fromType(s);
    }
}
