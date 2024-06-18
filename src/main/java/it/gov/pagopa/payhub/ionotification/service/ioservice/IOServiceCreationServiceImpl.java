package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IOServiceCreationServiceImpl implements IOServiceCreationService {

    private final IOServiceMapper ioServiceMapper;
    private final IODuplicateServiceHandlerService ioDuplicateServiceHandlerService;
    private final IOServiceRepository ioServiceRepository;
    private final IORestConnector connector;

    public IOServiceCreationServiceImpl(IOServiceMapper ioServiceMapper,
                                        IODuplicateServiceHandlerService ioDuplicateServiceHandlerService, IOServiceRepository ioServiceRepository, IORestConnector connector) {
        this.ioServiceMapper = ioServiceMapper;
        this.ioDuplicateServiceHandlerService = ioDuplicateServiceHandlerService;
        this.ioServiceRepository = ioServiceRepository;
        this.connector = connector;
    }

    @Override
    public void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        log.info("Save request of Service creation for {} and {}",
                serviceRequestDTO.getName(), serviceRequestDTO.getOrganization().getName());
        IOService service = ioServiceMapper.apply(enteId, tipoDovutoId, serviceRequestDTO);

        if (ioServiceRepository.createIfNotExists(service).getUpsertedId() == null) {
            handleExistingService(service, serviceRequestDTO);
        } else {
            log.info("Create new Service {} for {}",
                    service.getServiceName(), service.getOrganizationName());
            createService(serviceRequestDTO, service);
        }
    }

    private void handleExistingService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        String serviceId = ioDuplicateServiceHandlerService.searchIOService(service, serviceRequestDTO);
        if (serviceId != null) {
            log.info("Update service {} with serviceId: {}", service.getServiceName(), serviceId);
            ioServiceRepository.updateService(service, serviceId);
        } else {
            log.info("Create new Service {} for {} after not finding it in IO",
                    service.getServiceName(), service.getOrganizationName());
            createService(serviceRequestDTO, service);
        }
    }

    private void createService(ServiceRequestDTO serviceRequestDTO, IOService service) {
        log.info("Creating new service {} in IO", serviceRequestDTO.getName());
        ServiceResponseDTO responseDTO = connector.createService(serviceRequestDTO);
        ioServiceRepository.updateService(service, responseDTO.getId());
    }

}
