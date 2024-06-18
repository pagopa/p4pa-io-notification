package it.gov.pagopa.payhub.ionotification.service;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IODuplicateServiceHandlerService;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationServiceImpl;
import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceUtilityService;
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
    private IOServiceCreationServiceImpl service;
    @Mock
    IOServiceMapper serviceMapper;
    @Mock
    IOServiceUtilityService ioServiceUtilityService;
    @Mock
    IODuplicateServiceHandlerService ioDuplicateServiceHandlerService;


    @BeforeEach
    void setup(){
        service = new IOServiceCreationServiceImpl(
                serviceMapper, ioServiceUtilityService, ioDuplicateServiceHandlerService);
    }

    @Test
    void givenCreateServiceWhenFirstTryThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(new BsonObjectId(new ObjectId()));
        when(ioServiceUtilityService.createIfNotExists(ioService)).thenReturn(updateResult);

        when(ioServiceUtilityService.createService(serviceRequestDTO, ioService)).thenReturn(createServiceResponseDTO());
        doNothing().when(ioServiceUtilityService).updateService(ioService, "SERVICE_ID");

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceUtilityService, times(1)).updateService(ioService, "SERVICE_ID");
    }

    @Test
    void givenCreateServiceWhenRequestExistsThenHandleDuplicate(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        when(serviceMapper.apply(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO)).thenReturn(ioService);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(null);
        when(ioServiceUtilityService.createIfNotExists(ioService)).thenReturn(updateResult);

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceUtilityService, times(1)).createIfNotExists(ioService);
    }

}
