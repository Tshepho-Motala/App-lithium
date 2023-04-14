package lithium.service.casino.provider.evolution.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AuthenticationFailureResponse {
    private List<Errors> errors = new ArrayList<>();
}
