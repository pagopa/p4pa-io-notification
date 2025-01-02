package it.gov.pagopa.payhub.ionotification.event.producer;

import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IONotificationProducerTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private IONotificationProducer ioNotificationProducer;

    @InjectMocks
    private IONotificationProducer.IONotificationProducerConfig config;

    @BeforeEach
    public void setUp() {
        ioNotificationProducer = new IONotificationProducer(streamBridge);
    }

    @Test
    void testSendNotification() {
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();

        Supplier<Flux<Message<Object>>> supplier = config.notificationQueue();

        ioNotificationProducer.sendNotification(notificationQueueDTO);

        verify(streamBridge, times(1)).send(Mockito.eq("notificationQueue-out-0"), Mockito.any(), Mockito.eq(notificationQueueDTO));
        assertNotNull(supplier);
    }
}
