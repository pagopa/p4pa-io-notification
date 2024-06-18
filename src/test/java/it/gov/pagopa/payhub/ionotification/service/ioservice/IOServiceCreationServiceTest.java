package it.gov.pagopa.payhub.ionotification.service.ioservice;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IOServiceCreationServiceTest {

    public static final String ENTE_ID = "enteId";
    public static final String TIPO_DOVUTO_ID = "tipoDovutoId";
    public static final String SERVICE_ID = "SERVICE_ID";
    private IOServiceCreationServiceImpl service;
    @Mock
    IOServiceMapper serviceMapper;
    @Mock
    IOServiceRepository ioServiceRepository;
    @Mock
    IORestConnector connector;
    @Mock
    IOServiceSearchService ioServiceSearchService;


    @BeforeEach
    void setup(){
        service = new IOServiceCreationServiceImpl(
                serviceMapper, ioServiceSearchService, ioServiceRepository, connector);
    }

    @Test
    void givenCreateServiceWhenFirstTryThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(new BsonObjectId(new ObjectId()));
        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);
        when(ioServiceRepository.createIfNotExists(ioService)).thenReturn(updateResult);
        when(connector.createService(serviceRequestDTO)).thenReturn(createServiceResponseDTO());

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceRepository, times(1)).updateService(ioService, SERVICE_ID);
    }

    @Test
    void givenCreateServiceWhenRequestExistsAndServiceExistsInIOThenHandleDuplicate(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(null);
        when(ioServiceRepository.createIfNotExists(ioService)).thenReturn(updateResult);

        when(ioServiceSearchService.searchIOService(ioService, serviceRequestDTO))
                .thenReturn(SERVICE_ID);

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceRepository, times(1)).updateService(ioService, SERVICE_ID);
    }

    @Test
    void givenCreateServiceWhenRequestExistsAndServiceDoesNotExistsInIOThenHandleDuplicate(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(null);
        when(ioServiceRepository.createIfNotExists(ioService)).thenReturn(updateResult);

        when(ioServiceSearchService.searchIOService(ioService, serviceRequestDTO))
                .thenReturn(null);
        when(connector.createService(serviceRequestDTO)).thenReturn(createServiceResponseDTO());

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceRepository, times(1)).updateService(ioService, SERVICE_ID);
    }
}
