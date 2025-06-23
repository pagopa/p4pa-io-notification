package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;

public interface IOManageService {
    ServiceDTO getService(Long organizationId, Long debtPositionTypeOrgId);

    void deleteService(String serviceId);
}
