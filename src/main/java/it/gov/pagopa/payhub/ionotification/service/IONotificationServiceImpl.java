package it.gov.pagopa.payhub.ionotification.service;

import com.mongodb.MongoException;
import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.ServicePaginatedResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IOServiceRepository ioServiceRepository;
    private final IORestConnector connector;
    private final IOServiceMapper ioServiceMapper;

    public IONotificationServiceImpl(IOServiceRepository ioServiceRepository, IORestConnector connector, IOServiceMapper ioServiceMapper) {
        this.ioServiceRepository = ioServiceRepository;
        this.connector = connector;
        this.ioServiceMapper = ioServiceMapper;
    }

    @Override
    public void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        log.info("Save request of Service creation");
        IOService service = ioServiceMapper.apply(enteId, tipoDovutoId, serviceRequestDTO);

        try {
            service = ioServiceRepository.createIfNotExists(service);
            createNewService(serviceRequestDTO, service);

        } catch (MongoException e) {
            handleDuplicateService(service, serviceRequestDTO);
        }
    }

    private void createNewService(ServiceRequestDTO serviceRequestDTO, IOService service) {
        // handle errors from io
        log.info("Creating new service from IO");
        ServiceResponseDTO responseDTO = connector.createService(serviceRequestDTO);
        service.setServiceId(responseDTO.getId());
        ioServiceRepository.save(service);
    }

    private void handleDuplicateService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service request already exists, call IO to see if Service exists");

        // handle errors from io and variables
        ServicesListDTO servicesListDTO = connector.getAllServices(null, null);
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, servicesListDTO);

        if (existingServiceOpt.isPresent()) {
            log.info("Service found in IO, updating serviceId");
            ServicePaginatedResponseDTO existingService = existingServiceOpt.get();
            service.setServiceId(existingService.getId());
            ioServiceRepository.save(service);
        } else {
            log.info("Service not found in IO, creating new service");
            createNewService(serviceRequestDTO, service);
        }
    }

    private Optional<ServicePaginatedResponseDTO> findExistingService(IOService service, ServicesListDTO servicesListDTO) {
        return servicesListDTO.getServiceList().stream()
                .filter(existingService -> existingService.getServiceName().equals(service.getServiceName()) &&
                        existingService.getOrganization().getOrganizationName().equals(service.getOrganizationName()))
                .findFirst();
    }

}
