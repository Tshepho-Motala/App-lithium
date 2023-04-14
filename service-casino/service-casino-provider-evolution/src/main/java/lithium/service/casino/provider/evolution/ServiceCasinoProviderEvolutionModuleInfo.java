package lithium.service.casino.provider.evolution;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.evolution.configuration.ProviderConfigurationProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceCasinoProviderEvolutionModuleInfo extends ModuleInfoAdapter {

    public ServiceCasinoProviderEvolutionModuleInfo() {
        ProviderConfig providerConfig = ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.CASINO)
                .properties(getProviderProperties())
                .build();

        addProvider(providerConfig);
    }

    private List<ProviderConfigProperty> getProviderProperties() {
        List<ProviderConfigProperty> properties = new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigurationProperties.AUTH_TOKEN.getName())
                .tooltip(ProviderConfigurationProperties.AUTH_TOKEN.getTooltip())
                .required(true)
                .dataType(String.class)
                .version(1)
                .build()
        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigurationProperties.STARTGAME_BASE_URL.getName())
                        .tooltip(ProviderConfigurationProperties.STARTGAME_BASE_URL.getTooltip())
                        .required(true)
                        .dataType(String.class)
                        .version(1)
                        .build()
        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ProviderConfigurationProperties.EVOLUTION_AUTH_ENDPOINT.getName())
                        .tooltip(ProviderConfigurationProperties.EVOLUTION_AUTH_ENDPOINT.getTooltip())
                        .required(true)
                        .dataType(String.class)
                        .version(1)
                        .build()
        );

        return properties;
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/casino/mock/**").permitAll()
                .antMatchers("/games/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers("/games/{domainName}/startGame").access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers("/api/**").permitAll();
    }


}
