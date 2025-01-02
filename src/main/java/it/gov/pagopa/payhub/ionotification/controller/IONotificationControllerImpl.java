package it.gov.pagopa.payhub.ionotification.controller;

import it.gov.pagopa.payhub.ionotification.controller.generated.IoNotificationApi;
import it.gov.pagopa.payhub.ionotification.service.IOService;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IONotificationControllerImpl implements IoNotificationApi {

    private final IOService ioService;

    public IONotificationControllerImpl(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public ResponseEntity<Void> createService(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        ioService.createService(enteId, tipoDovutoId, serviceRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioService.sendMessage(notificationQueueDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ServiceDTO> getService(Long enteId, Long tipoDovutoId) {
        ServiceDTO serviceDTO = ioService.getService(enteId, tipoDovutoId);
        return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteService(String serviceId) {
        ioService.deleteService(serviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteNotification(String userId, Long enteId, Long tipoDovutoId) {
        ioService.deleteNotification(userId, enteId, tipoDovutoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
