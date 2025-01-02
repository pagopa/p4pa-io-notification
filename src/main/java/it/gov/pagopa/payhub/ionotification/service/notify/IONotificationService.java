package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;

public interface IONotificationService {

    void sendMessage(NotificationQueueDTO notificationQueueDTO);
    void sendNotification(NotificationQueueDTO notificationQueueDTO);
    void deleteNotification(String userId, Long enteId, Long tipoDovutoId);
}
