package it.gov.pagopa.payhub.ionotification.service.ioservice;

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

    private final IOServiceUtilityService ioServiceUtilityService;

    public IODuplicateServiceHandlerServiceImpl(IOServiceUtilityService ioServiceUtilityService) {
        this.ioServiceUtilityService = ioServiceUtilityService;
    }

    @Override
    public void handleDuplicateService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service request already exists, call IO to see if Service exists");

        ServicesListDTO servicesListDTO = ioServiceUtilityService.getAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, servicesListDTO);

        if (existingServiceOpt.isPresent()) {
            log.info("Service found in IO, updating serviceId");
            ServicePaginatedResponseDTO existingService = existingServiceOpt.get();
            ioServiceUtilityService.updateService(service, existingService.getId());
        } else {
            log.info("Service not found in IO, creating new service");
            ioServiceUtilityService.createService(serviceRequestDTO, service);
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
