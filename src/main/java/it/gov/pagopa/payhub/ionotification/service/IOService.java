package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.dto.generated.*;

public interface IOService {

    void createService(Long organizationId, Long debtPositionTypeOrgId, ServiceRequestDTO serviceRequestDTO);

    MessageResponseDTO sendMessage(String accessToken, NotificationRequestDTO notificationRequestDTO);

    ServiceDTO getService(Long organizationId, Long debtPositionTypeOrgId);

    void deleteService(String serviceId);

    void deleteNotification(String notificationId);

}
