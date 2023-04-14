package lithium.service.casino.provider.evolution.service.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
@Data
@Slf4j
public class EvolutionAuthenticationRestService {
    private EvolutionConfiguration authenticationConfiguration;
    private RestTemplate restTemplate;

    @Autowired
    public EvolutionAuthenticationRestService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder,
                                              EvolutionConfiguration evolutionConfiguration
    ){
        log.trace("EvolutionAuthenticationRestService | configuration: {}", evolutionConfiguration);
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(evolutionConfiguration.getTimeoutInMilliseconds()))
                .build();
    }
}
