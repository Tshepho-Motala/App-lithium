package lithium.service.kyc.provider.onfido;

import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.document.client.EnableDocumentClient;
import lithium.service.kyc.provider.onfido.config.ProviderConfigService;
import lithium.service.kyc.provider.onfido.inbox.OnfidoNotificationService;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.event.EventListener;

@LithiumService
@EnableEurekaClient
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableDocumentClient
@EnableChangeLogService
@EnableNotificationStream
@EnableLimitInternalSystemClient
@EnableCustomHttpErrorCodeExceptions
@AllArgsConstructor
public class ServiceKycOnfidoProvider extends LithiumServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceKycOnfidoProvider.class, args);
    }

    private final OnfidoNotificationService notificationService;
    private final ProviderConfigService configService;

    @EventListener
    @Override
    public void startup(ApplicationStartedEvent e)
            throws Exception {
        super.startup(e);
        notificationService.registerNotificationTypes();
        configService.getSupportedDomains()
                .forEach(notificationService::registerBiometricValidationPassedNotification);
    }
}
