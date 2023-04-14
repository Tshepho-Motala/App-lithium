package lithium.service.casino.provider.evolution.configuration;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.evolution.ServiceCasinoProviderEvolutionModuleInfo;
import lithium.service.casino.provider.evolution.exception.PropertyNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderConfigurationService {

    private final LithiumServiceClientFactory lithiumServiceClientFactory;

    private final ServiceCasinoProviderEvolutionModuleInfo serviceCasinoProviderEvolutionModuleInfo;

    public ProviderConfiguration getEvolutionProviderConfig(String domainName) throws Status512ProviderNotConfiguredException {
        ProviderClient providerClient = getProviderClientService(domainName);
        checkIfProviderConfigured(providerClient, domainName);
        List<ProviderProperty> providerProperties = getProviderProperties(providerClient, domainName);
        return populateProviderConfigurations(providerProperties);
    }

    private ProviderConfiguration populateProviderConfigurations(List<ProviderProperty> providerProperties) {
        return ProviderConfiguration.builder()
                .authToken(getValue(ProviderConfigurationProperties.AUTH_TOKEN, providerProperties))
                .startGameUrl(getValue(ProviderConfigurationProperties.STARTGAME_BASE_URL, providerProperties))
                .evolutionAuthUrl(getValue(ProviderConfigurationProperties.EVOLUTION_AUTH_ENDPOINT, providerProperties))
                .build();
    }

    private List<ProviderProperty> getProviderProperties(ProviderClient providerClient, String domainName) throws Status512ProviderNotConfiguredException {
        Response<Iterable<ProviderProperty>> providerProperties = providerClient.propertiesByProviderUrlAndDomainName(
                serviceCasinoProviderEvolutionModuleInfo.getModuleName(),
                domainName
        );
        if (!providerProperties.isSuccessful() || providerProperties.getData() == null){
            throw new Status512ProviderNotConfiguredException(domainName);
        }
        return (List<ProviderProperty>) providerProperties.getData();
    }

    private void checkIfProviderConfigured(ProviderClient providerClient, String domainName) throws Status512ProviderNotConfiguredException {
        Response<Provider> providerResponse = providerClient.findByUrlAndDomainName(serviceCasinoProviderEvolutionModuleInfo.getModuleName(), domainName);
        if(!providerResponse.isSuccessful() || providerResponse.getData() == null || !providerResponse.getData().getEnabled()){
            throw new Status512ProviderNotConfiguredException(domainName);
        }
    }

    private ProviderClient getProviderClientService(String domainName) throws Status512ProviderNotConfiguredException {
        try {
            return lithiumServiceClientFactory.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException clientFactoryException){
            throw new Status512ProviderNotConfiguredException(domainName);
        }
    }

    private static String getValue(ProviderConfigurationProperties property, List<ProviderProperty> providerProperties) {
        return providerProperties.stream()
                .filter(pp -> property.getName().equalsIgnoreCase(pp.getName()))
                .findFirst()
                .orElseThrow(() -> new PropertyNotConfiguredException(format("Property=%s is not configured in BO",
                        property.getName())))
                .getValue();
    }
}
