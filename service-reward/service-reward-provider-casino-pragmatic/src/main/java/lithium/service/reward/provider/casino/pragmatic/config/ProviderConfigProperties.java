package lithium.service.reward.provider.casino.pragmatic.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum ProviderConfigProperties implements Serializable {
  REWARDS_BASE_URL("rewardsBaseUrl"),
  PRAGMATIC_USERNAME("secureLogin"),
  MAX_REWARD_LIFETIME_IN_DAYS("maxRewardLifetimeInDays"),
  HASH_SECRET("hashSecret"),
  PLAYER_GUID_PREFIX("playerGuidPrefix"),
  PLAYER_OFFSET("playerOffset");
  @Getter
  @Accessors( fluent = true )
  private String value;
}