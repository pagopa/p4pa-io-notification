package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.ionotification.service.notify.IONotificationService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IOServiceTest {

    public static final String ENTE_ID = "enteId";
    public static final String TIPO_DOVUTO_ID = "tipoDovutoId";

    @Mock
    IOServiceCreationService ioServiceCreationService;

    @Mock
    IONotificationService ioNotificationService;

    private IOService service;

    @BeforeEach
    void setup(){
        service = new IOServiceImpl(ioServiceCreationService, ioNotificationService);
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
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();

        doNothing().when(ioNotificationService)
                .sendMessage(notificationQueueDTO);

        service.sendMessage(notificationQueueDTO);

        verify(ioNotificationService, times(1)).sendMessage(notificationQueueDTO);

    }

}
