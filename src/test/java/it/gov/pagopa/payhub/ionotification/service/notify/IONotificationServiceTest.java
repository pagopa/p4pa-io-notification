package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.KeysDTO;
import it.gov.pagopa.payhub.ionotification.dto.NotificationResource;
import it.gov.pagopa.payhub.ionotification.dto.ProfileResource;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.event.producer.IONotificationProducer;
import it.gov.pagopa.payhub.ionotification.exception.custom.SenderNotAllowedException;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants.*;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    public static final long TIME_TO_LIVE = 3600L;
    private IONotificationService service;

    @Mock
    private IONotificationRepository ioNotificationRepository;
    @Mock
    private IONotificationProducer ioNotificationProducer;
    @Mock
    private IORestConnector connector;
    @Mock
    private IONotificationMapper ioNotificationMapper;
    @Mock
    private IOServiceRepository ioServiceRepository;
    private IOService ioService;
    private KeysDTO keysDTO;
    private NotificationQueueDTO notificationQueueDTO;
    private IONotification ioNotification;
    private FiscalCodeDTO fiscalCodeDTO;

    @BeforeEach
    void setup(){
        service = new IONotificationServiceImpl(ioNotificationRepository, connector, ioNotificationProducer, ioNotificationMapper,
                ioServiceRepository, TIME_TO_LIVE, SUBJECT, MARKDOWN);
        ioService = mapIoService(createServiceRequestDTO());
        keysDTO = getTokenIOResponse();
        notificationQueueDTO = mapToSendMessageToQueue();
        ioNotification = mapIONotification();
        fiscalCodeDTO = getUserProfileRequest();
    }

    @Test
    void givenSendMessageThenSendToQueue(){
        doNothing().when(ioNotificationProducer).sendNotification(notificationQueueDTO);

        service.sendMessage(notificationQueueDTO);

        verify(ioNotificationProducer, times(1)).sendNotification(notificationQueueDTO);
    }

    @Test
    void givenSendNotificationThenSuccess(){
        mockServiceAndObtainIOToken();

        when(connector.getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey()))
                .thenReturn(getUserProfileResponse());

        when(ioNotificationMapper.mapToQueue(FISCAL_CODE, TIME_TO_LIVE, "Test Subject SERVICE_NAME", MARKDOWN)).thenReturn(sendNotificationRequest());

        when(connector.sendNotification(sendNotificationRequest(), keysDTO.getPrimaryKey()))
                .thenReturn(new NotificationResource("ID"));

        sendNotification(NOTIFICATION_STATUS_OK);

        assertEquals("ID", ioNotification.getNotificationId());
        assertEquals(ORGANIZATION_NAME, ioNotification.getEnteName());
        assertEquals(SERVICE_NAME, ioNotification.getTipoDovutoName());

    }

    @Test
    void givenSendNotificationWhenServiceNotFoundThenSaveKO(){

        when(ioServiceRepository.findByEnteIdAndTipoDovutoId(ENTE_ID, TIPO_DOVUTO_ID)).thenReturn(Optional.empty());

        sendNotification(NOTIFICATION_STATUS_KO_SERVICE_NOT_FOUND);

        assertNull(ioNotification.getNotificationId());
        assertNull(ioNotification.getEnteName());
        assertNull(ioNotification.getTipoDovutoName());

    }

    @Test
    void givenSendNotificationWhenSenderIsNotAllowedThenSaveKO(){
        mockServiceAndObtainIOToken();

        when(connector.getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey()))
                .thenReturn(new ProfileResource(false, new ArrayList<>()));

        sendNotification(NOTIFICATION_STATUS_KO_SENDER_NOT_ALLOWED);

        assertNull(ioNotification.getNotificationId());

    }

    @Test
    void givenSendNotificationWhenSenderNotAllowedExceptionThenSaveKO(){
        mockServiceAndObtainIOToken();

        doThrow(new SenderNotAllowedException("Error")).when(connector).getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey());

        sendNotification(NOTIFICATION_STATUS_KO_SENDER_NOT_ALLOWED);

        assertNull(ioNotification.getNotificationId());

    }

    private void sendNotification(String status) {
        when(ioNotificationMapper.mapToSaveNotification(notificationQueueDTO, status))
                .thenReturn(ioNotification);

        service.sendNotification(notificationQueueDTO);

        verify(ioNotificationRepository, times(1)).save(ioNotification);

        if (!status.equals(NOTIFICATION_STATUS_KO_SERVICE_NOT_FOUND)) {
            assertEquals(ORGANIZATION_NAME, ioNotification.getEnteName());
            assertEquals(SERVICE_NAME, ioNotification.getTipoDovutoName());
        }
    }

    private void mockServiceAndObtainIOToken() {
        ioService.setServiceId(SERVICE_ID);

        when(ioServiceRepository.findByEnteIdAndTipoDovutoId(ENTE_ID, TIPO_DOVUTO_ID)).thenReturn(Optional.of(ioService));

        when(connector.getServiceKeys(SERVICE_ID)).thenReturn(keysDTO);

        when(ioNotificationMapper.mapToGetProfile(notificationQueueDTO)).thenReturn(fiscalCodeDTO);
    }
}