package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceAlreadyDeletedException;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceNotFoundException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public ServiceDTO getService(Long enteId, Long tipoDovutoId) {
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
        if (service.isPresent()) {
            try {
                log.info("Deleting service {} from IO", serviceId);
                ioRestConnector.deleteService(serviceId);
            } catch (ServiceNotFoundException | ServiceAlreadyDeletedException e) {
                log.info("Service with serviceId {} does not exists or was already deleted from IO", serviceId);
            } finally {
                log.info("Deleting service {}", serviceId);
                ioServiceRepository.delete(service.get());
            }
        }else {
            log.info("Service with serviceId {} was not found or is already deleted", serviceId);
        }
    }
}
