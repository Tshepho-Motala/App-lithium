package lithium.service.kyc.provider.onfido.inbox;

import lithium.service.notifications.client.dtos.INotificationType;

public enum NotificationTypes implements INotificationType {
    BIOMETRIC_VERIFICATION_PASSED("biometric_verification_passed");

    private String type;

    NotificationTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
