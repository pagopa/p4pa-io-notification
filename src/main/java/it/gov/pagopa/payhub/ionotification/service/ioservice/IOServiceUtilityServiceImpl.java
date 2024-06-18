package it.gov.pagopa.payhub.ionotification.service.ioservice;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IOServiceUtilityServiceImpl implements IOServiceUtilityService {

    private final IOServiceRepository ioServiceRepository;
    private final IORestConnector connector;

    public IOServiceUtilityServiceImpl(IOServiceRepository ioServiceRepository, IORestConnector connector) {
        this.ioServiceRepository = ioServiceRepository;
        this.connector = connector;
    }

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO, IOService service) {
        log.info("Creating new service from IO");
        return connector.createService(serviceRequestDTO);
    }

    @Override
    public ServicesListDTO getAllServices() {
        log.info("Get all services service from IO");
        return connector.getAllServices();
    }

    @Override
    public void updateService(IOService service, String serviceId) {
        log.info("Updating service: {}", service.getServiceName());
        ioServiceRepository.updateService(service, serviceId);
    }

    @Override
    public UpdateResult createIfNotExists(IOService service) {
        log.info("Creating service {} if it do not exists", service.getServiceName());
        return ioServiceRepository.createIfNotExists(service);
    }
}
