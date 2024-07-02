package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceAlreadyDeletedException;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceNotFoundException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.enums.ServiceStatus.DELETED;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IOManageServiceTest {

    public static final Long TIPO_DOVUTO_ID = 456L;
    public static final Long ENTE_ID = 123L;
    private IOManageService service;
    @Mock
    IOServiceMapper serviceMapper;
    @Mock
    IOServiceRepository ioServiceRepository;
    @Mock
    IORestConnector ioRestConnector;

    @BeforeEach
    void setup(){
        service = new IOManageServiceImpl(serviceMapper, ioServiceRepository, ioRestConnector);
    }

    @Test
    void givenGetServiceThenSuccess(){
        IOService serviceModel = mapIoService(createServiceRequestDTO());
        when(ioServiceRepository.findByEnteIdAndTipoDovutoId(ENTE_ID, TIPO_DOVUTO_ID))
                .thenReturn(Optional.of(serviceModel));

        when(serviceMapper.mapService(serviceModel)).thenReturn(getServiceResponse());

        ServiceDTO serviceDTO = service.getService(ENTE_ID, TIPO_DOVUTO_ID);

        assertNotNull(serviceDTO);
        verify(ioServiceRepository, times(1)).findByEnteIdAndTipoDovutoId(ENTE_ID, TIPO_DOVUTO_ID);
    }

    @Test
    void givenGetServiceWhenServiceNotFoundThenThrowServiceNotFoundException(){

        when(ioServiceRepository.findByEnteIdAndTipoDovutoId(ENTE_ID, TIPO_DOVUTO_ID))
                .thenReturn(Optional.empty());

        ServiceNotFoundException exception =assertThrows(ServiceNotFoundException.class, () ->
                service.getService(ENTE_ID, TIPO_DOVUTO_ID));

        assertEquals("The service for 456 associated with 123 does not exist", exception.getMessage());
    }

    @Test
    void givenDeleteServiceThenSuccess(){
        IOService ioService = mapIoService(createServiceRequestDTO());
        when(ioServiceRepository.findByServiceId(SERVICE_ID)).thenReturn(Optional.of(ioService));

        doNothing().when(ioRestConnector).deleteService(SERVICE_ID);

        when(ioServiceRepository.save(ioService)).thenReturn(ioService);

        service.deleteService(SERVICE_ID);

        assertEquals(DELETED, ioService.getStatus());
        verify(ioServiceRepository, times(1)).findByServiceId(SERVICE_ID);
        verify(ioServiceRepository, times(1)).save(ioService);
    }

    @Test
    void givenDeleteServiceWhenServiceNotFoundThenThrowServiceNotFoundException(){
        when(ioServiceRepository.findByServiceId(SERVICE_ID)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () ->
                service.deleteService(SERVICE_ID));

        verify(ioServiceRepository, times(1)).findByServiceId(SERVICE_ID);
    }

    @Test
    void givenDeleteServiceWhenServiceAlreadyDeletedThenThrowServiceAlreadyDeletedException(){
        IOService ioService = mapIoService(createServiceRequestDTO());
        ioService.setStatus(DELETED);

        when(ioServiceRepository.findByServiceId(SERVICE_ID)).thenReturn(Optional.of(ioService));

        assertThrows(ServiceAlreadyDeletedException.class, () ->
                service.deleteService(SERVICE_ID));

        verify(ioServiceRepository, times(1)).findByServiceId(SERVICE_ID);
    }

    @Test
    void givenDeleteServiceWhenServiceAlreadyDeletedFromIOThenUpdateServiceStatus(){
        IOService ioService = mapIoService(createServiceRequestDTO());

        when(ioServiceRepository.findByServiceId(SERVICE_ID)).thenReturn(Optional.of(ioService));

        doThrow(ServiceAlreadyDeletedException.class).when(ioRestConnector).deleteService(SERVICE_ID);

        when(ioServiceRepository.save(ioService)).thenReturn(ioService);

        service.deleteService(SERVICE_ID);

        assertEquals(DELETED, ioService.getStatus());
        verify(ioServiceRepository, times(1)).findByServiceId(SERVICE_ID);
        verify(ioServiceRepository, times(1)).save(ioService);
    }
}
