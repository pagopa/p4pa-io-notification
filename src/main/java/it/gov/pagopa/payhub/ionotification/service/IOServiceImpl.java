package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.dto.generated.*;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOManageService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
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
    public MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO) {
        return ioNotificationService.sendMessage(notificationRequestDTO);
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
    public void deleteNotification(String userId, Long enteId, Long tipoDovutoId) {
        ioNotificationService.deleteNotification(userId, enteId, tipoDovutoId);
    }
}
