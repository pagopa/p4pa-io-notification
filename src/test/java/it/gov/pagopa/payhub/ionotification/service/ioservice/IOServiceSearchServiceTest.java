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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IOServiceSearchServiceTest {

    @Mock
    IORestConnector connector;

    private IOServiceSearchService service;

    @BeforeEach
    void setup(){
        service = new IOServiceSearchServiceImpl(connector);
    }

    @Test
    void givenSearchIOServiceWhenServiceExistsInIOThenGetAllServices() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO firstPage = new ServicesListDTO(
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(0, 10)),
                new PaginationDTO(0, 99, 10));

        when(connector.getAllServices(99, 0)).thenReturn(firstPage);

        assertTrue(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be present");
    }


    @Test
    void givenSearchIOServiceWhenServiceDoesNotExistsInIOThenDoNothing() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO emptyPage = new ServicesListDTO(new ArrayList<>(), new PaginationDTO(0, 99, 0));

        when(connector.getAllServices(99, 0)).thenReturn(emptyPage);

        assertFalse(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be empty");
    }

}
