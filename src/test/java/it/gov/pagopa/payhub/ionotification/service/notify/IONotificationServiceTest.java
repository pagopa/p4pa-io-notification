package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.event.producer.IONotificationProducer;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    private IONotificationService service;

    @Mock
    private IONotificationProducer IONotificationProducer;

    @BeforeEach
    void setup(){
        service = new IONotificationServiceImpl(IONotificationProducer);
    }

    @Test
    void givenSendMessageThenSendToQueue(){
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();
        doNothing().when(IONotificationProducer).sendNotification(notificationQueueDTO);

        service.sendMessage(notificationQueueDTO);

        verify(IONotificationProducer, times(1)).sendNotification(notificationQueueDTO);
    }
}