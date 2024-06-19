package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.event.producer.IONotificationProducer;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IONotificationProducer ioNotificationProducer;

    public IONotificationServiceImpl(IONotificationProducer ioNotificationProducer) {
        this.ioNotificationProducer = ioNotificationProducer;
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        log.info("Sending message to notify of type {}", notificationQueueDTO.getOperationType());
        ioNotificationProducer.sendNotification(notificationQueueDTO);
    }

}
