package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
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

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IOManageServiceTest {

    public static final String ENTE_ID = "enteId";
    public static final String TIPO_DOVUTO_ID = "tipoDovutoId";
    private IOManageService service;
    @Mock
    IOServiceMapper serviceMapper;
    @Mock
    IOServiceRepository ioServiceRepository;

    @BeforeEach
    void setup(){
        service = new IOManageServiceImpl(serviceMapper, ioServiceRepository);
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

        assertEquals("The service for tipoDovutoId associated with enteId does not exist", exception.getMessage());
    }
}
