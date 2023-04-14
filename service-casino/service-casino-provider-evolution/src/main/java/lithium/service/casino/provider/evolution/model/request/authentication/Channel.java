package lithium.service.casino.provider.evolution.model.request.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {

    private Boolean wrapped;
    private Boolean mobile;

}
