package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.PaginationDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicePaginatedResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IOServiceSearchServiceImpl implements IOServiceSearchService {

    private final IORestConnector connector;
    private int offset;
    private final int limit;

    public IOServiceSearchServiceImpl(IORestConnector connector,
                                      @Value("${rest-client.backend-io-manage.service.offset}") int offset,
                                      @Value("${rest-client.backend-io-manage.service.limit}") int limit) {
        this.connector = connector;
        this.offset = offset;
        this.limit = limit;
    }


    @Override
    public Optional<String> searchIOService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service {} for {} request already exists, call IO to see if Service exists",
                service.getServiceName(), service.getOrganizationName());
        List<ServicePaginatedResponseDTO> services = retrieveAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, services);

        return existingServiceOpt.map(ServicePaginatedResponseDTO::getId);
    }

    private List<ServicePaginatedResponseDTO> retrieveAllServices() {
        List<ServicePaginatedResponseDTO> allServices = new ArrayList<>();
        boolean morePages = true;

        while (morePages) {
            ServicesListDTO servicesListDTO = connector.getAllServices(limit, offset);

            if (servicesListDTO!=null) {
                allServices.addAll(servicesListDTO.getServiceList());
                PaginationDTO pagination = servicesListDTO.getPagination();

                offset += pagination.getCount();
                morePages = pagination.getCount() >= pagination.getLimit();
            }else {
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
