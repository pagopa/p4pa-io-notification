package it.gov.pagopa.payhub.ionotification.service.ioservice;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.PaginationDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServicesListDTO;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
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
        service = new IOServiceSearchServiceImpl(connector, 0, 5);
    }


    @Test
    void givenSearchIOServiceWhenServiceExistsInIOThenGetAllServices() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO allServicesResponse = getAllServicesResponse();
        ServicesListDTO firstPage = new ServicesListDTO(
                new ArrayList<>(allServicesResponse.getServiceList().subList(0, 5)),
                new PaginationDTO(0, 5, 5));
        ServicesListDTO secondPage = new ServicesListDTO(
                new ArrayList<>(allServicesResponse.getServiceList().subList(5, 8)),
                new PaginationDTO(5, 5, 3));

        when(connector.getAllServices(5, 0)).thenReturn(firstPage);
        when(connector.getAllServices(5, 5)).thenReturn(secondPage);

        assertTrue(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be present");
    }

    @Test
    void givenSearchIOServiceWhenServiceExistsInIOAndLastPageCountIsEqualToLimitThenGetAllServices() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO firstPage = new ServicesListDTO(
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(0, 5)),
                new PaginationDTO(0, 5, 5));

        when(connector.getAllServices(5, 0)).thenReturn(firstPage);

        assertTrue(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be present");
    }

    @Test
    void givenSearchIOServiceWhenGetAllServicesThenWrongServiceName() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        serviceRequestDTO.setName("WRONG_NAME");
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO firstPage = getServicesListDTO();

        getServiceEmpty(firstPage, ioService, serviceRequestDTO);
    }

    @Test
    void givenSearchIOServiceWhenGetAllServicesThenWrongOrganizationName() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        serviceRequestDTO.getOrganization().setName("WRONG_ORGANIZATION_NAME");
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO firstPage = getServicesListDTO();

        getServiceEmpty(firstPage, ioService, serviceRequestDTO);
    }


    @Test
    void givenSearchIOServiceWhenGetAllServicesThenStatusDeleted() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        serviceRequestDTO.setName("SERVICE_NAME3");
        serviceRequestDTO.getOrganization().setName("Organization3");
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO firstPage = getServicesListDTO();

        getServiceEmpty(firstPage, ioService, serviceRequestDTO);
    }

    @Test
    void givenSearchIOServiceWhenServiceDoesNotExistsInIOThenDoNothing() {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        IOService ioService = mapIoService(serviceRequestDTO);

        ServicesListDTO emptyPage = new ServicesListDTO(new ArrayList<>(), new PaginationDTO(0, 5, 0));

        getServiceEmpty(emptyPage, ioService, serviceRequestDTO);
    }

    private static ServicesListDTO getServicesListDTO() {
        return new ServicesListDTO(
                new ArrayList<>(getAllServicesResponse().getServiceList().subList(0, 10)),
                new PaginationDTO(0, 5, 5));
    }

    private void getServiceEmpty(ServicesListDTO firstPage, IOService ioService, ServiceRequestDTO serviceRequestDTO) {
        when(connector.getAllServices(5, 0)).thenReturn(firstPage);

        assertFalse(service.searchIOService(ioService, serviceRequestDTO).isPresent(),
                "Expected service to be empty");
    }

}
