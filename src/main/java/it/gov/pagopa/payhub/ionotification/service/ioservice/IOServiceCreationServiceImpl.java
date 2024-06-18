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
        log.info("Save request of Service creation");
        IOService service = ioServiceMapper.apply(enteId, tipoDovutoId, serviceRequestDTO);

        if (ioServiceRepository.createIfNotExists(service).getUpsertedId() == null) {
            handleExistingService(service, serviceRequestDTO);
        } else {
            createService(serviceRequestDTO, service);
        }
    }

    private void handleExistingService(IOService service, ServiceRequestDTO serviceRequestDTO) {
        String serviceId = ioDuplicateServiceHandlerService.handleDuplicateService(service, serviceRequestDTO);
        if (serviceId != null) {
            ioServiceRepository.updateService(service, serviceId);
        } else {
            createService(serviceRequestDTO, service);
        }
    }

    private void createService(ServiceRequestDTO serviceRequestDTO, IOService service) {
        log.info("Creating new service from IO");
        ServiceResponseDTO responseDTO = connector.createService(serviceRequestDTO);
        ioServiceRepository.updateService(service, responseDTO.getId());
    }

}
