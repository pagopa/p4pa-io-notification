package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.PaginationDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IODuplicateServiceHandlerServiceTest {

    public static final String SERVICE_ID = "SERVICE_ID";
    @Mock
    IORestConnector connector;

    private IODuplicateServiceHandlerService service;

    @BeforeEach
    void setup(){
        service = new IODuplicateServiceHandlerServiceImpl(connector);
    }

    @Test
    void givenHandleDuplicateServiceWhenServiceExistsInIOThenGetAllServices(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO allServicesResponse = getAllServicesResponse();
        when(connector.getAllServices()).thenReturn(allServicesResponse);

        service.handleDuplicateService(ioService, serviceRequestDTO);

        assertEquals(SERVICE_ID, allServicesResponse.getServiceList().get(0).getId());
    }

    @Test
    void givenHandleDuplicateServiceWhenServiceDoesNotExistsInIOThenDoNothing(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        IOService ioService = mapIoService(serviceRequestDTO);

        when(connector.getAllServices())
                .thenReturn(new ServicesListDTO(new ArrayList<>(), new PaginationDTO()));

        assertNull(service.handleDuplicateService(ioService, serviceRequestDTO));
    }
}
