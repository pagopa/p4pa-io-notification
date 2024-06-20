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
public class IOServiceSearchServiceImpl implements IOServiceSearchService {

    private final IORestConnector connector;

    public IOServiceSearchServiceImpl(IORestConnector connector) {
        this.connector = connector;
    }

    @Override
    public Optional<String> searchIOService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service request already exists, call IO to see if Service exists");
        ServicesListDTO servicesListDTO = connector.getAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, servicesListDTO);

        return existingServiceOpt.map(ServicePaginatedResponseDTO::getId);
    }


    private Optional<ServicePaginatedResponseDTO> findExistingService(IOService service, ServicesListDTO servicesListDTO) {
        return servicesListDTO.getServiceList().stream()
                .filter(existingService -> existingService.getServiceName().equals(service.getServiceName()) &&
                        existingService.getOrganization().getOrganizationName().equals(service.getOrganizationName()) &&
                        !existingService.getStatus().getValue().equals("deleted"))
                .findFirst();
    }
}
