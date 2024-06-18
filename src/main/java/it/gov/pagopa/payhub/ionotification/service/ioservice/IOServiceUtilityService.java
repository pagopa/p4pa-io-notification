package it.gov.pagopa.payhub.ionotification.service.ioservice;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IOServiceUtilityService {
    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO, IOService service);

    ServicesListDTO getAllServices();

    void updateService(IOService service, String serviceId);

    UpdateResult createIfNotExists(IOService service);

}
