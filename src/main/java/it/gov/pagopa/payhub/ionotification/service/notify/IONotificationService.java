package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;

public interface IONotificationService {

    MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO);
    void deleteNotification(String userId, Long enteId, Long tipoDovutoId);
}
