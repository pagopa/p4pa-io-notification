package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceNotFoundException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;

import java.util.Optional;

public class IOManageServiceImpl implements IOManageService {

    private final IOServiceMapper ioServiceMapper;
    private final IOServiceRepository ioServiceRepository;

    public IOManageServiceImpl(IOServiceMapper ioServiceMapper, IOServiceRepository ioServiceRepository) {
        this.ioServiceMapper = ioServiceMapper;
        this.ioServiceRepository = ioServiceRepository;
    }

    @Override
    public ServiceDTO getService(String enteId, String tipoDovutoId) {
        Optional<IOService> service = ioServiceRepository.findByEnteIdAndTipoDovutoId(enteId, tipoDovutoId);
        if (service.isEmpty()){
            throw new ServiceNotFoundException(String.format(
                    "The service for %s associated with %s does not exist", tipoDovutoId, enteId));
        }
        return ioServiceMapper.mapService(service.get());
    }
}
