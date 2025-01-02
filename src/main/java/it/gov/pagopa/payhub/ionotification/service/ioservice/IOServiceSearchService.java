package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;

import java.util.Optional;

public interface IOServiceSearchService {
    Optional<String> searchIOService(IOService service, ServiceRequestDTO serviceRequestDTO);
}
