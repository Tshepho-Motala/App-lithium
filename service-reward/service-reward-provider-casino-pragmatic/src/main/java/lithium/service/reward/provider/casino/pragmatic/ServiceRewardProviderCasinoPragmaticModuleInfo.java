package lithium.service.reward.provider.casino.pragmatic;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.reward.provider.casino.pragmatic.config.ProviderConfigProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceRewardProviderCasinoPragmaticModuleInfo extends ModuleInfoAdapter {

    public ServiceRewardProviderCasinoPragmaticModuleInfo() {
        super();

        List<ProviderConfigProperty> properties = new ArrayList<>();
        properties.add(ProviderConfigProperty.builder()
              .name(ProviderConfigProperties.REWARDS_BASE_URL.value())
              .required(true)
              .tooltip("Pragmatic Play Base URL")
              .dataType(String.class)
              .build()
        );

        properties.add(ProviderConfigProperty.builder()
              .name(ProviderConfigProperties.PRAGMATIC_USERNAME.value())
              .required(true)
              .tooltip("User name for authentication/authorization to the Casino Game API service on Pragmatic")
              .dataType(String.class)
              .build()
        );

        properties.add(ProviderConfigProperty.builder()
              .name(ProviderConfigProperties.MAX_REWARD_LIFETIME_IN_DAYS.value())
              .required(true)
              .tooltip("Maximum duration in days before the reward expires")
              .dataType(String.class)
              .build()
        );

        properties.add(ProviderConfigProperty.builder()
              .name(ProviderConfigProperties.HASH_SECRET.value())
              .required(true)
              .tooltip("Secret key used to calculate hash")
              .dataType(String.class)
              .build()
        );

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PLAYER_GUID_PREFIX.value())
                .required(true)
                .tooltip("The iforium to pragmatic guid prefix")
                .dataType(String.class)
                .build()
        );

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PLAYER_OFFSET.value())
                .required(false)
                .tooltip("Used to calculate an offset for playerId")
                .dataType(String.class)
                .build()
        );

        ProviderConfig providerConfig = ProviderConfig.builder()
              .name(getModuleName())
              .type(ProviderConfig.ProviderType.REWARD)
              .properties(properties)
              .build();

        addProvider(providerConfig);
    }
    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        // @formatter:off
        http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
        // @formatter:on
    }
}
