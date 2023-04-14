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
public enum ExclusiveUploadStatus {
    CREATED("created"),
    UPLOADING("uploading"),
    FAILED("failed"),
    DONE("done");

    @JsonValue
    private final String status;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExclusiveUploadStatus fromStatus(String status) {
        return Arrays.stream(values()).filter(s -> s.status.equalsIgnoreCase(status))
                .findFirst()
                .orElse(null);
    }
}
