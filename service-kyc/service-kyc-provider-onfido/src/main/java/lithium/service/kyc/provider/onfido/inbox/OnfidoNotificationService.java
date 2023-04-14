package lithium.service.kyc.provider.onfido.inbox;

import lithium.service.notifications.client.objects.Channel;
import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.client.objects.NotificationChannel;
import lithium.service.notifications.client.objects.NotificationType;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OnfidoNotificationService {
    private final NotificationStream notificationStream;

    public void sendBiometricValidationPassedNotification(String guid) {
        notificationStream.process(UserNotification.builder()
                .userGuid(guid)
                .notificationName(NotificationTypes.BIOMETRIC_VERIFICATION_PASSED.getType())
                .phReplacements(Collections.emptyList())
                .cta(true)
                .metaData(new HashMap<>())
                .build());
        log.info("Biometric validation passed notification sent to " + guid);
    }

    public void registerBiometricValidationPassedNotification(String domainName) {
        NotificationChannel notificationChannel = NotificationChannel
                .builder()
                .channel(
                        Channel.builder()
                                .name(lithium.service.notifications.client.enums.Channel.PULL.channelName())
                                .build())
                .templateLang("en")
                .templateName("")
                .forced(true)
                .build();
        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(notificationChannel);

        Notification notification = Notification.builder()
                .systemNotification(false)
                .name(NotificationTypes.BIOMETRIC_VERIFICATION_PASSED.getType())
                .displayName("Biometric validation passed")
                .description("Document facial similarity passed via Onfido service")
                .message("Document facial similarity passed")
                .domain(lithium.service.notifications.client.objects.Domain.builder().name(domainName).build())
                .channels(channels)
                .notificationType(NotificationType.builder()
                        .name(NotificationTypes.BIOMETRIC_VERIFICATION_PASSED.getType())
                        .build())
                .build();

        notificationStream.createOrUpdateNotification(notification);
        log.info("Biometric validation passed notification registered for " + domainName);
    }

    public void registerNotificationTypes() {
        notificationStream.registerNotificationType(NotificationTypes.BIOMETRIC_VERIFICATION_PASSED.getType());
        log.info("Biometric validation passed notification type registered");
    }
}
