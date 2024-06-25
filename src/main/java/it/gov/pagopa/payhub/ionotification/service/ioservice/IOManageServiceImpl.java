package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceNotFoundException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants.SERVICE_STATUS_DELETED;

@Service
@Slf4j
public class IOManageServiceImpl implements IOManageService {

    private final IOServiceMapper ioServiceMapper;
    private final IOServiceRepository ioServiceRepository;
    private final IORestConnector ioRestConnector;

    public IOManageServiceImpl(IOServiceMapper ioServiceMapper, IOServiceRepository ioServiceRepository, IORestConnector ioRestConnector) {
        this.ioServiceMapper = ioServiceMapper;
        this.ioServiceRepository = ioServiceRepository;
        this.ioRestConnector = ioRestConnector;
    }

    @Override
    public ServiceDTO getService(String enteId, String tipoDovutoId) {
        Optional<IOService> service = ioServiceRepository.findByEnteIdAndTipoDovutoId(enteId, tipoDovutoId);
        if (service.isEmpty()){
            log.error("Service for {} associated with {} was not found", tipoDovutoId, enteId);
            throw new ServiceNotFoundException(String.format(
                    "The service for %s associated with %s does not exist", tipoDovutoId, enteId));
        }
        log.info("Service {} associated with {} found", service.get().getServiceName(), service.get().getOrganizationName());
        return ioServiceMapper.mapService(service.get());
    }

    @Override
    public void deleteService(String serviceId) {
        Optional<IOService> service = ioServiceRepository.findByServiceId(serviceId);

        if (service.isEmpty()){
            log.error("Service with serviceId {} was not found", serviceId);
            throw new ServiceNotFoundException(String.format(
                    "The service with serviceId %s does not exist", serviceId));
        }
        deleteServiceFromIO(serviceId, service.get());
    }

    private void deleteServiceFromIO(String serviceId, IOService service) {
        log.info("Delete service {} associated with {} from IO", service.getServiceName(), service.getOrganizationName());
        ioRestConnector.deleteService(serviceId);

        String status = SERVICE_STATUS_DELETED;
        service.setStatus(status);
        log.info("Update service {} with status {}", service.getServiceName(), status);
        ioServiceRepository.save(service);
    }
}
