package it.gov.pagopa.payhub.ionotification.event.consumer;

import it.gov.pagopa.payhub.ionotification.service.IOService;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class IONotificationConsumer {

    @Bean
    Consumer<NotificationQueueDTO> notificationConsumer(IOService service){
        return service::sendNotification;
    }

}
