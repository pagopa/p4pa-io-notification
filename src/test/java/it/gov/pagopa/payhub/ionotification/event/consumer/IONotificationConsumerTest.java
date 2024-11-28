package it.gov.pagopa.payhub.ionotification.event.consumer;

import it.gov.pagopa.payhub.ionotification.service.IOService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IONotificationConsumerTest {

    @Mock
    private IOService ioService;
    @InjectMocks
    private IONotificationConsumer ioNotificationConsumer;
    private Consumer<NotificationQueueDTO> consumerIONotification;

    @BeforeEach
    void setUp(){
        consumerIONotification = ioNotificationConsumer.notificationConsumer(ioService);
    }

    @Test
    void whenNotificationConsumerThenSuccess(){
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();
        consumerIONotification.accept(notificationQueueDTO);
        verify(ioService, times(1)).sendNotification(notificationQueueDTO);
    }
}
