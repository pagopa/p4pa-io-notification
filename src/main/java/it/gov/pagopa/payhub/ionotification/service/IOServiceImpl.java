package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOManageService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
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
    public void createService(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        ioServiceCreationService.createService(enteId, tipoDovutoId, serviceRequestDTO);
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioNotificationService.sendMessage(notificationQueueDTO);
    }

    @Override
    public ServiceDTO getService(Long enteId, Long tipoDovutoId) {
        return ioManageService.getService(enteId, tipoDovutoId);
    }

    @Override
    public void deleteService(String serviceId) {
        ioManageService.deleteService(serviceId);
    }

    @Override
    public void sendNotification(NotificationQueueDTO notificationQueueDTO) {
        ioNotificationService.sendNotification(notificationQueueDTO);
    }

    @Override
    public void deleteNotification(String userId, Long enteId, Long tipoDovutoId) {
        ioNotificationService.deleteNotification(userId, enteId, tipoDovutoId);
    }
}
