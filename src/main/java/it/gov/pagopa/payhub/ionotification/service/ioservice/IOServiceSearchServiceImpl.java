package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.PaginationDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicePaginatedResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        List<ServicePaginatedResponseDTO> services = retrieveAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, services);

        return existingServiceOpt.map(ServicePaginatedResponseDTO::getId);
    }

    private List<ServicePaginatedResponseDTO> retrieveAllServices() {
        List<ServicePaginatedResponseDTO> allServices = new ArrayList<>();
        boolean morePages = true;
        int offset = 0;
        int limit = 99;

        while (morePages) {
            ServicesListDTO servicesListDTO = connector.getAllServices(limit, offset);

            allServices.addAll(servicesListDTO.getServiceList());
            PaginationDTO pagination = servicesListDTO.getPagination();

            offset += pagination.getCount();
            if (pagination.getCount() < pagination.getLimit()) {
                morePages = false;
            }
        }

        return allServices;
    }

    private Optional<ServicePaginatedResponseDTO> findExistingService(IOService service, List<ServicePaginatedResponseDTO> servicePaginatedResponseDTOS) {
        return servicePaginatedResponseDTOS.stream()
                .filter(existingService -> existingService.getServiceName().equals(service.getServiceName()) &&
                        existingService.getOrganization().getOrganizationName().equals(service.getOrganizationName()) &&
                        !existingService.getStatus().getValue().equals("deleted"))
                .findFirst();
    }
}
