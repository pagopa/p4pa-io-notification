package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOManageService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IOServiceTest {

    public static final Long TIPO_DOVUTO_ID = 456L;
    public static final Long ENTE_ID = 123L;

    @Mock
    IOServiceCreationService ioServiceCreationService;

    @Mock
    IONotificationService ioNotificationService;

    @Mock
    IOManageService ioManageService;

    private IOService service;

    @BeforeEach
    void setup(){
        service = new IOServiceImpl(ioServiceCreationService, ioNotificationService, ioManageService);
    }

    @Test
    void givenCreateServiceThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        doNothing().when(ioServiceCreationService)
                .createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceCreationService, times(1)).createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

    }

    @Test
    void givenSendMessageThenSuccess(){
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();

        doNothing().when(ioNotificationService)
                .sendMessage(notificationRequestDTO);

        service.sendMessage(notificationRequestDTO);

        verify(ioNotificationService, times(1)).sendMessage(notificationRequestDTO);

    }

    @Test
    void givenGetServiceThenSuccess(){

        when(ioManageService.getService(ENTE_ID, TIPO_DOVUTO_ID)).thenReturn(getServiceResponse());

        ServiceDTO serviceDTO = service.getService(ENTE_ID, TIPO_DOVUTO_ID);

        assertNotNull(serviceDTO);

    }

    @Test
    void givenDeleteServiceThenSuccess(){
        doNothing().when(ioManageService).deleteService(SERVICE_ID);

        service.deleteService(SERVICE_ID);

        verify(ioManageService, times(1)).deleteService(SERVICE_ID);
    }


    @Test
    void givenDeleteNotificationThenSuccess(){
        doNothing().when(ioNotificationService).deleteNotification(USER_ID, ENTE_ID, TIPO_DOVUTO_ID);

        service.deleteNotification(USER_ID, ENTE_ID, TIPO_DOVUTO_ID);

        verify(ioNotificationService, times(1)).deleteNotification(USER_ID, ENTE_ID, TIPO_DOVUTO_ID);
    }

}
