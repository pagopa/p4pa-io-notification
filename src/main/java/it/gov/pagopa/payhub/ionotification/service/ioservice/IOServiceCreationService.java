package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;

public interface IOServiceCreationService {

    void createService(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO);
}
