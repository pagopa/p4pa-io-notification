package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IOService {

    void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO);

    void sendMessage(NotificationQueueDTO notificationQueueDTO);

    ServiceDTO getService(String enteId, String tipoDovutoId);

}
