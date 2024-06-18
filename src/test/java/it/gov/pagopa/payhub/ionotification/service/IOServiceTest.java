package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.service.ioservice.IOServiceCreationService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IOServiceTest {

    public static final String ENTE_ID = "enteId";
    public static final String TIPO_DOVUTO_ID = "tipoDovutoId";

    @Mock
    IOServiceCreationService ioServiceCreationService;

    private IOService service;

    @BeforeEach
    void setup(){
        service = new IOServiceImpl(ioServiceCreationService);
    }

    @Test
    void givenCreateServiceThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        doNothing().when(ioServiceCreationService)
                .createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        service.createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        verify(ioServiceCreationService, times(1)).createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

    }

}
