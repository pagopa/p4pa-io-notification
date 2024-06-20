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
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(0, 99)),
                new PaginationDTO(0, 99, 99));
        ServicesListDTO secondPage = new ServicesListDTO(
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(99, 198)),
                new PaginationDTO(99, 99, 99));
        ServicesListDTO thirdPage = new ServicesListDTO(
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(198, 201)),
                new PaginationDTO(198, 99, 3));

        when(connector.getAllServices(99, 0)).thenReturn(firstPage);
        when(connector.getAllServices(99, 99)).thenReturn(secondPage);
        when(connector.getAllServices(99, 198)).thenReturn(thirdPage);

        assertTrue(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be present");
    }


    @Test
    void givenSearchIOServiceWhenServiceDoesNotExistsInIOThenDoNothing() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO emptyPage = new ServicesListDTO(new ArrayList<>(), new PaginationDTO(0, 20, 0));

        when(connector.getAllServices(99, 0)).thenReturn(emptyPage);

        assertFalse(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be empty");
    }

}
