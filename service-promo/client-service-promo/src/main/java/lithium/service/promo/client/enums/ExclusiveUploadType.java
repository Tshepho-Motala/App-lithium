package lithium.service.promo.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum ExclusiveUploadType {
    ADD("add"),
    REMOVE("remove");

    @JsonValue
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExclusiveUploadType fromType(String type) {
        return Arrays.stream(values()).filter(s -> s.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
