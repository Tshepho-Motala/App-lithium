package lithium.service.kyc.provider.onfido.config;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProviderConfigService {

    @Autowired
    private LithiumServiceClientFactory services;

    @Autowired
    private ModuleInfo moduleInfo;

    public ProviderConfig getConfig(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        ProviderClient providerService = getProviderService();
        Response<Iterable<ProviderProperty>> providerPropertiesResponse =
                providerService.propertiesByProviderUrlAndDomainName(moduleInfo.getModuleName(), domainName);

        if (!providerPropertiesResponse.isSuccessful() || providerPropertiesResponse.getData() == null) {
            log.error("Onfido not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("Onfido");
        }

        ProviderConfig config = new ProviderConfig();

        for (ProviderProperty providerProperty : providerPropertiesResponse.getData()) {
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.BASE_API_URL.getValue())) {
                config.setBaseUrl(providerProperty.getValue());
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.API_TOKEN.getValue())) {
                config.setApiToken(providerProperty.getValue());
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.REPORT_NAMES.getValue())) {
                config.setReportNames(Arrays.stream(providerProperty.getValue().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList()));
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.WEBHOOK_IDS.getValue())) {
                String ids = providerProperty.getValue();
                if (ids != null && !ids.isEmpty()) {
                    config.setWebhookIds(ids.split(","));
                }
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.MATCH_DOCUMENT_ADDRESS.getValue())) {
                config.setMatchDocumentAddress(Boolean.parseBoolean(providerProperty.getValue()));
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.MATCH_FIRST_NAME.getValue())) {
                config.setMatchFirstName(Boolean.parseBoolean(providerProperty.getValue()));
            } else if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.SUPPORTED_ISSUING_COUNTRIES.getValue())) {
                String countries = providerProperty.getValue();
                if (countries != null && !countries.isEmpty()) {
                    config.setSupportedIssuingCountries(Arrays.stream(countries.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList()));
                }
            }
        }

        if (config.getBaseUrl() == null || config.getApiToken() == null || config.getReportNames() == null) {
            log.error("Onfido not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("Onfido");
        }

        return config;
    }

    private ProviderClient getProviderService() throws Status500InternalServerErrorException {
        try {
            return services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Fail to get provider properties", e);
            throw new Status500InternalServerErrorException("Can't get service-domain provider client");
        }
    }

    public List<String> getSupportedDomains() throws Exception {
        ProviderClient providerService = getProviderService();
        Response<List<Provider>> providersResponse = providerService.listEnabledProvidersByUrl(moduleInfo.getModuleName());
        if (providersResponse.isSuccessful()) {
            return providersResponse.getData().stream()
                    .map(Provider::getDomain)
                    .map(Domain::getName)
                    .toList();
        }
        log.error("Can't retrieve providers due " + providersResponse.getMessage());
        throw new Exception("Can't retrieve providers due " + providersResponse.getMessage());
    }

}
