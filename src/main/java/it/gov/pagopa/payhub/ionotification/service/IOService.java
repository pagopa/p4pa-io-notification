package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IOService {

    void createService(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO);

    void sendMessage(NotificationQueueDTO notificationQueueDTO);

    ServiceDTO getService(Long enteId, Long tipoDovutoId);

    void deleteService(String serviceId);

    void sendNotification(NotificationQueueDTO notificationQueueDTO);

    void deleteNotification(String fiscalCode, Long enteId, Long tipoDovutoId);

}
