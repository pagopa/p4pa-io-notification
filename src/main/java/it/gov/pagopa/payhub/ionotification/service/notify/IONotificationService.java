package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;

public interface IONotificationService {

    void sendMessage(NotificationQueueDTO notificationQueueDTO);
    void sendNotification(NotificationQueueDTO notificationQueueDTO);
    void deleteNotification(String fiscalCode, Long enteId, Long tipoDovutoId);
}
