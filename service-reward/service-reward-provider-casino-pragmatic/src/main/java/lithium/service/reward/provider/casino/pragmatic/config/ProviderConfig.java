package lithium.service.reward.provider.casino.pragmatic.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderConfig implements Serializable {
  @Serial
  private static final long serialVersionUID = -3557868937556391581L;
  private String rewardsBaseUrl;
  private String pragmaticUserName;
  private Integer maxRewardLifetimeInDays;
  private String hashSecret;
  private String playerGuidPrefix;
  private String playerOffset;
}
