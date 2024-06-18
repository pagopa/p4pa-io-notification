package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IODuplicateServiceHandlerService {
    String searchIOService(IOService service, ServiceRequestDTO serviceRequestDTO);
}
