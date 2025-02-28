package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.KeysDTO;
import it.gov.pagopa.payhub.ionotification.dto.NotificationResource;
import it.gov.pagopa.payhub.ionotification.dto.ProfileResource;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.exception.custom.SenderNotAllowedException;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
import it.gov.pagopa.payhub.ionotification.service.UserIdObfuscatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.KO_SENDER_NOT_ALLOWED;
import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    public static final long TIME_TO_LIVE = 3600L;
    private IONotificationService service;

    @Mock
    private IONotificationRepository ioNotificationRepositoryMock;
    @Mock
    private IORestConnector connectorMock;
    @Mock
    private IONotificationMapper ioNotificationMapperMock;
    @Mock
    private UserIdObfuscatorService obfuscatorServiceMock;

    private IOService ioService;
    private KeysDTO keysDTO;
    private NotificationRequestDTO notificationRequestDTO;
    private IONotification ioNotification;
    private FiscalCodeDTO fiscalCodeDTO;

    @BeforeEach
    void setup() {
        service = new IONotificationServiceImpl(
                ioNotificationRepositoryMock,
                connectorMock,
                ioNotificationMapperMock,
                obfuscatorServiceMock,
                TIME_TO_LIVE);

        ioService = mapIoService(createServiceRequestDTO());
        keysDTO = getTokenIOResponse();
        notificationRequestDTO = buildNotificationRequestDTO();
        ioNotification = mapIONotification();
        fiscalCodeDTO = getUserProfileRequest();
    }

    @Test
    void givenSendNotificationThenSuccess() {
        mockServiceAndObtainIOToken();

        when(connectorMock.getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey()))
                .thenReturn(getUserProfileResponse());

        when(ioNotificationMapperMock.map(FISCAL_CODE, TIME_TO_LIVE, "Test Subject SERVICE_NAME", MARKDOWN)).thenReturn(sendNotificationRequest());

        when(connectorMock.sendNotification(sendNotificationRequest(), keysDTO.getPrimaryKey()))
                .thenReturn(new NotificationResource("ID"));

        sendNotification(OK);

        assertEquals("ID", ioNotification.getNotificationId());

    }

    @Test
    void givenSendNotificationWhenSenderIsNotAllowedThenSaveKO() {
        mockServiceAndObtainIOToken();

        when(connectorMock.getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey()))
                .thenReturn(new ProfileResource(false, new ArrayList<>()));

        sendNotification(KO_SENDER_NOT_ALLOWED);

        assertNull(ioNotification.getNotificationId());

    }

    @Test
    void givenSendNotificationWhenSenderNotAllowedExceptionThenSaveKO() {
        mockServiceAndObtainIOToken();

        doThrow(new SenderNotAllowedException("Error")).when(connectorMock).getProfile(fiscalCodeDTO, keysDTO.getPrimaryKey());

        sendNotification(KO_SENDER_NOT_ALLOWED);

        assertNull(ioNotification.getNotificationId());

    }

    @Test
    void givenDeleteNotificationThenSuccess() {
        when(ioNotificationRepositoryMock.findByUserIdAndEnteIdAndTipoDovutoId(USER_ID, ORG_ID, DEBT_POSITION_TYPE_ORG_ID))
                .thenReturn(Optional.of(ioNotification));

        service.deleteNotification(USER_ID, ORG_ID, DEBT_POSITION_TYPE_ORG_ID);

        verify(ioNotificationRepositoryMock, times(1)).delete(any(IONotification.class));
        verify(ioNotificationRepositoryMock, times(1)).delete(ioNotification);
    }

    @Test
    void givenDeleteNotificationWhenNotificationDoesNotExistThenDoNothing() {
        when(ioNotificationRepositoryMock.findByUserIdAndEnteIdAndTipoDovutoId(USER_ID, ORG_ID, DEBT_POSITION_TYPE_ORG_ID))
                .thenReturn(Optional.empty());

        service.deleteNotification(USER_ID, ORG_ID, DEBT_POSITION_TYPE_ORG_ID);

        verify(ioNotificationRepositoryMock, times(0)).delete(any(IONotification.class));
        verify(ioNotificationRepositoryMock, times(1)).findByUserIdAndEnteIdAndTipoDovutoId(USER_ID, ORG_ID, DEBT_POSITION_TYPE_ORG_ID);
    }

    private void sendNotification(NotificationStatus status) {
        mockEncryptFiscalCode();
        when(ioNotificationMapperMock.mapToSaveNotification(notificationRequestDTO, status, USER_ID))
                .thenReturn(ioNotification);

        service.sendMessage(notificationRequestDTO);

        verify(ioNotificationRepositoryMock, times(1)).save(ioNotification);
    }

    private void mockServiceAndObtainIOToken() {
        ioService.setServiceId(SERVICE_ID);


        when(connectorMock.getServiceKeys(SERVICE_ID)).thenReturn(keysDTO);

        when(ioNotificationMapperMock.mapToGetProfile(notificationRequestDTO)).thenReturn(fiscalCodeDTO);
    }

    private void mockEncryptFiscalCode() {
        when(obfuscatorServiceMock.obfuscate(FISCAL_CODE)).thenReturn(USER_ID);
    }
}