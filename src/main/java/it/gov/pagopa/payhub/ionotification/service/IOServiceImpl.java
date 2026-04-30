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
    public void createService(Long organizationId, Long debtPositionTypeOrgId, ServiceRequestDTO serviceRequestDTO) {
        ioServiceCreationService.createService(organizationId, debtPositionTypeOrgId, serviceRequestDTO);
    }

    @Override
    public MessageResponseDTO sendMessage(String accessToken, NotificationRequestDTO notificationRequestDTO) {
        return ioNotificationService.sendMessage(accessToken, notificationRequestDTO);
    }

    @Override
    public ServiceDTO getService(Long organizationId, Long debtPositionTypeOrgId) {
        return ioManageService.getService(organizationId, debtPositionTypeOrgId);
    }

    @Override
    public void deleteService(String serviceId) {
        ioManageService.deleteService(serviceId);
    }

    @Override
    public void deleteNotification(String notificationId) {
        ioNotificationService.deleteNotification(notificationId);
    }
}
