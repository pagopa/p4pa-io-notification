//package it.gov.pagopa.payhub.ionotification.service.notify;
//
//import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
//import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
//import it.gov.pagopa.payhub.ionotification.event.producer.IONotificationProducer;
//import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
//import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
//import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.times;
//
//@ExtendWith(MockitoExtension.class)
//class IONotificationServiceTest {
//
//    private IONotificationService service;
//
//    @Mock
//    private IONotificationRepository ioNotificationRepository;
//    @Mock
//    private IONotificationProducer ioNotificationProducer;
//    @Mock
//    private IORestConnector connector;
//
//    @Mock
//    private IONotificationMapper ioNotificationMapper;
//    @Mock
//    private IOServiceRepository ioServiceRepository;
//
//    @BeforeEach
//    void setup(){
//        service = new IONotificationServiceImpl(ioNotificationRepository,
//                connector, ioNotificationProducer, ioNotificationMapper, ioServiceRepository);
//    }
//
//    @Test
//    void givenSendMessageThenSendToQueue(){
//        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();
//        doNothing().when(ioNotificationProducer).sendNotification(notificationQueueDTO);
//
//        service.sendMessage(notificationQueueDTO);
//
//        verify(ioNotificationProducer, times(1)).sendNotification(notificationQueueDTO);
//    }
//}