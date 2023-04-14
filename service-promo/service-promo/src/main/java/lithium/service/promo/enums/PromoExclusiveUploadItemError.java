package lithium.service.promo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum PromoExclusiveUploadItemError {
    DUPLICATE_PLAYER("Player is already on the exclusive list."),
    PLAYER_NOT_FOUND("Player could not be found."),
    PLAYER_NOT_ON_THE_LIST("Player not found on the exclusive list");

    private final String error;
}
