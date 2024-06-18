package it.gov.pagopa.payhub.ionotification.service;

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

        if (ioServiceRepository.createIfNotExists(service).getUpsertedId() == null){
            handleDuplicateService(service, serviceRequestDTO);
            return;
        }

        createNewService(serviceRequestDTO, service);
    }

    private void createNewService(ServiceRequestDTO serviceRequestDTO, IOService service) {
        log.info("Creating new service from IO");
        ServiceResponseDTO responseDTO = connector.createService(serviceRequestDTO);
        ioServiceRepository.updateService(service, responseDTO.getId());
    }

    private void handleDuplicateService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        log.info("Service request already exists, call IO to see if Service exists");

        ServicesListDTO servicesListDTO = connector.getAllServices();
        Optional<ServicePaginatedResponseDTO> existingServiceOpt = findExistingService(service, servicesListDTO);

        if (existingServiceOpt.isPresent()) {
            log.info("Service found in IO, updating serviceId");
            ServicePaginatedResponseDTO existingService = existingServiceOpt.get();
            ioServiceRepository.updateService(service, existingService.getId());
        } else {
            log.info("Service not found in IO, creating new service");
            createNewService(serviceRequestDTO, service);
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
