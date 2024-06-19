package it.gov.pagopa.payhub.ionotification.controller;

import it.gov.pagopa.payhub.controller.generated.IoNotificationApi;
import it.gov.pagopa.payhub.ionotification.service.IOService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
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
    public ResponseEntity<Void> createService(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        ioService.createService(enteId, tipoDovutoId, serviceRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioService.sendMessage(notificationQueueDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
