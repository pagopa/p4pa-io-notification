package it.gov.pagopa.payhub.ionotification.service;

import com.mongodb.MongoException;
import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.PaginationDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    public static final String ENTE_ID = "enteId";
    public static final String TIPO_DOVUTO_ID = "tipoDovutoId";
    private IONotificationServiceImpl service;

    @Mock
    IORestConnector ioRestConnector;

    @Mock
    IOServiceMapper serviceMapper;

    @Mock
    IOServiceRepository repository;

    @BeforeEach
    void setup(){
        service = new IONotificationServiceImpl(repository, ioRestConnector, serviceMapper);
    }

    @Test
    void givenCreateServiceWhenFirstTryThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);
        when(repository.createIfNotExists(ioService)).thenReturn(ioService);
        when(ioRestConnector.createService(serviceRequestDTO)).thenReturn(createServiceResponseDTO());

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(repository, times(1)).save(ioService);
    }

    @Test
    void givenCreateServiceWhenRequestExistsThenCallIOAndFindService(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        doThrow(new MongoException("Error"))
                .when(repository).createIfNotExists(ioService);

        when(ioRestConnector.getAllServices()).thenReturn(getAllServicesResponse());

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(repository, times(1)).save(ioService);
    }

    @Test
    void givenCreateServiceWhenRequestExistsThenCallIOAndNotFindService(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        doThrow(new MongoException("Error"))
                .when(repository).createIfNotExists(ioService);

        when(ioRestConnector.getAllServices()).thenReturn(new ServicesListDTO(new ArrayList<>(), new PaginationDTO()));

        when(ioRestConnector.createService(serviceRequestDTO)).thenReturn(createServiceResponseDTO());

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(repository, times(1)).save(ioService);
    }


}
