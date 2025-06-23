package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;

public interface IOServiceCreationService {

    void createService(Long organizationId, Long debtPositionTypeOrgId, ServiceRequestDTO serviceRequestDTO);
}
