package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOManageService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IOServiceImpl implements IOService {

    private final IOServiceCreationService ioServiceCreationService;
    private final IONotificationService ioNotificationService;
    private final IOManageService ioService;

    @Autowired
    public IOServiceImpl(IOServiceCreationService ioServiceCreationService, IONotificationService ioNotificationService, IOManageService ioService) {
        this.ioServiceCreationService = ioServiceCreationService;
        this.ioNotificationService = ioNotificationService;
        this.ioService = ioService;
    }

    @Override
    public void createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        ioServiceCreationService.createService(enteId, tipoDovutoId, serviceRequestDTO);
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioNotificationService.sendMessage(notificationQueueDTO);

    }

    @Override
    public ServiceDTO getService(String enteId, String tipoDovutoId) {
        return ioService.getService(enteId, tipoDovutoId);
    }
}
