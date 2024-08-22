package it.gov.pagopa.payhub.ionotification.event.producer;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Component
public class IONotificationProducer {

    @Value("${spring.cloud.stream.bindings.notificationQueue-out-0.binder}")
    private String binder;

    private final StreamBridge streamBridge;

    public IONotificationProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Configuration
    static class IONotificationProducerConfig {
        @Bean
        public Supplier<Flux<Message<Object>>> notificationQueue() {
            return Flux::empty;
        }
    }

    public void sendNotification(NotificationQueueDTO notificationQueueDTO){
        streamBridge.send("notificationQueue-out-0", binder, notificationQueueDTO);
    }
}
