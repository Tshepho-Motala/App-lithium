package lithium.service.casino.provider.evolution.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Errors {
    private String code;
    private String message;
}
