package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IONotificationService {

    void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO);
}
