package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.dto.generated.*;

public interface IOService {

    void createService(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO);

    MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO);

    ServiceDTO getService(Long enteId, Long tipoDovutoId);

    void deleteService(String serviceId);

    void deleteNotification(String userId, Long enteId, Long tipoDovutoId);

}
