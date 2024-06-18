package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IOServiceImpl implements IOService {

    private final IOServiceCreationService ioServiceCreationService;

    @Autowired
    public IOServiceImpl(IOServiceCreationService ioServiceCreationService) {
        this.ioServiceCreationService = ioServiceCreationService;
    }

    @Override
    public void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        ioServiceCreationService.createService(enteId, tipoDovutoId, serviceRequestDTO);
    }
}
