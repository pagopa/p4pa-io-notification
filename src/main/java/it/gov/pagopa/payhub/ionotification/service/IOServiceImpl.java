package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOManageService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class IOServiceImpl implements IOService {

    private final IOServiceCreationService ioServiceCreationService;
    private final IONotificationService ioNotificationService;
    private final IOManageService ioManageService;

    public IOServiceImpl(IOServiceCreationService ioServiceCreationService, IONotificationService ioNotificationService, IOManageService ioManageService) {
        this.ioServiceCreationService = ioServiceCreationService;
        this.ioNotificationService = ioNotificationService;
        this.ioManageService = ioManageService;
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
        return ioManageService.getService(enteId, tipoDovutoId);
    }

    @Override
    public void deleteService(String serviceId) {
        ioManageService.deleteService(serviceId);
    }
}
