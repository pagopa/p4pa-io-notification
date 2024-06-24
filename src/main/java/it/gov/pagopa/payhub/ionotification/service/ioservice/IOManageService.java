package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.model.generated.ServiceDTO;

public interface IOManageService {
    ServiceDTO getService(String enteId, String tipoDovutoId);
}
