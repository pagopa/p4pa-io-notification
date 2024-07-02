package it.gov.pagopa.payhub.ionotification.constants;

import lombok.Getter;

public class IONotificationConstants {

    @Getter
    public enum ServiceStatus{
        SERVICE_STATUS_CREATED("CREATED"),
        SERVICE_STATUS_REQUESTED("REQUESTED"),
        SERVICE_STATUS_DELETED("DELETED");

        private final String value;

        ServiceStatus(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum NotificationStatus {
        NOTIFICATION_STATUS_OK("OK"),
        NOTIFICATION_STATUS_KO_SERVICE_NOT_FOUND("KO_SERVICE_NOT_FOUND"),
        NOTIFICATION_STATUS_KO_SENDER_NOT_ALLOWED("KO_SENDER_NOT_ALLOWED");

        private final String value;

        NotificationStatus(String value) {
            this.value = value;
        }
    }

    private IONotificationConstants() {
    }
}
