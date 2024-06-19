package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.event.producer.NotificationProducer;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final NotificationProducer notificationProducer;

    public IONotificationServiceImpl(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        notificationProducer.sendNotification(notificationQueueDTO);
    }

}
