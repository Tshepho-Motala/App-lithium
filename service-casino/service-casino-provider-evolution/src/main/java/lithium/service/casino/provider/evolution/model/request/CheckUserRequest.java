package lithium.service.casino.provider.evolution.model.request;

import lithium.service.casino.provider.evolution.model.request.authentication.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserRequest {

    private String userId;

    private String sid;

    private Channel channel;

    private String uuid;

}
