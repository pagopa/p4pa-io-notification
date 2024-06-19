package it.gov.pagopa.payhub.ionotification.event.producer;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    @Value("${spring.cloud.stream.bindings.notificationQueue-out-0.binder}")
    private String binder;

    private final StreamBridge streamBridge;

    public NotificationProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendNotification(NotificationQueueDTO notificationQueueDTO){
        streamBridge.send("notificationQueue-out-0", binder, notificationQueueDTO);
    }
}
