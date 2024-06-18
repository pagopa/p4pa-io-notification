package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IOServiceCreationServiceImpl implements IOServiceCreationService {

    private final IOServiceMapper ioServiceMapper;
    private final IOServiceUtilityService ioServiceUtilityService;
    private final IODuplicateServiceHandlerService ioDuplicateServiceHandlerService;

    public IOServiceCreationServiceImpl(IOServiceMapper ioServiceMapper,
                                        IOServiceUtilityService ioServiceUtilityService,
                                        IODuplicateServiceHandlerService ioDuplicateServiceHandlerService) {
        this.ioServiceMapper = ioServiceMapper;
        this.ioServiceUtilityService = ioServiceUtilityService;
        this.ioDuplicateServiceHandlerService = ioDuplicateServiceHandlerService;
    }

    @Override
    public void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        log.info("Save request of Service creation");
        IOService service = ioServiceMapper.apply(enteId, tipoDovutoId, serviceRequestDTO);

        if (ioServiceUtilityService.createIfNotExists(service).getUpsertedId() == null) {

            ioDuplicateServiceHandlerService.handleDuplicateService(service, serviceRequestDTO);

        } else {
            ServiceResponseDTO serviceResponseDTO = ioServiceUtilityService.createService(serviceRequestDTO, service);
            ioServiceUtilityService.updateService(service, serviceResponseDTO.getId());
        }
    }

}
