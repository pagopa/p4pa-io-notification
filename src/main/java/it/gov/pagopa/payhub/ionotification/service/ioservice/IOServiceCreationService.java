package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IOServiceCreationService {

    void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO);
}
