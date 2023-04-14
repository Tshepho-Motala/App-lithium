package lithium.service.casino.provider.evolution.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserResponse {

    private String sid;

    private String uuid;

    private String status;

}
