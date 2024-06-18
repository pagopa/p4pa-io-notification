package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.ServicePaginatedResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IODuplicateServiceHandlerServiceImpl implements  IODuplicateServiceHandlerService {

    private final IORestConnector connector;

    public IODuplicateServiceHandlerServiceImpl(IORestConnector connector) {
        this.connector = connector;
    }

    @Override
    public String handleDuplicateService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service request already exists, call IO to see if Service exists");

        ServicesListDTO servicesListDTO = connector.getAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, servicesListDTO);

        if (existingServiceOpt.isPresent()) {
            log.info("Service found in IO, updating serviceId");
            return existingServiceOpt.get().getId();
        } else {
            log.info("Service not found in IO, creating new service");
            return null;
        }
    }


    private Optional<ServicePaginatedResponseDTO> findExistingService(IOService service, ServicesListDTO servicesListDTO) {
        return servicesListDTO.getServiceList().stream()
                .filter(existingService -> existingService.getServiceName().equals(service.getServiceName()) &&
                        existingService.getOrganization().getOrganizationName().equals(service.getOrganizationName()) &&
                        !existingService.getStatus().getValue().equals("deleted"))
                .findFirst();
    }
}
