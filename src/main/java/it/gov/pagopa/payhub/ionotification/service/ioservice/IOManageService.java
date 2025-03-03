package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;

public interface IOManageService {
    ServiceDTO getService(Long enteId, Long tipoDovutoId);

    void deleteService(String serviceId);
}
