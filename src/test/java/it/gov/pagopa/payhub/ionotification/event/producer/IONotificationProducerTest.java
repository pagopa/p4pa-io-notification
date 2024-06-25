package it.gov.pagopa.payhub.ionotification.event.producer;

import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class IONotificationProducerTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private IONotificationProducer ioNotificationProducer;

    @BeforeEach
    public void setUp() {
        ioNotificationProducer = new IONotificationProducer(streamBridge);
    }

    @Test
    void testSendNotification() {
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();

        ioNotificationProducer.sendNotification(notificationQueueDTO);

        verify(streamBridge, times(1)).send(Mockito.eq("notificationQueue-out-0"), Mockito.any(), Mockito.eq(notificationQueueDTO));
    }
}
