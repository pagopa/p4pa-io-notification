package it.gov.pagopa.payhub.ionotification.mapper;

import it.gov.pagopa.payhub.ionotification.dto.mapper.IOServiceMapper;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapIoService;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IOServiceMapper.class)
class IOServiceMapperTest {

    @Autowired
    IOServiceMapper ioServiceMapper;

    @Test
    void whenApplyMapperThenSuccess(){
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        IOService actual = ioServiceMapper.apply("ENTE_ID", "TIPO_DOVUTO_ID", serviceRequestDTO);

        assertNotNull(actual);
    }

    @Test
    void whenMapServiceThenSuccess(){
        IOService serviceModel = mapIoService(createServiceRequestDTO());

        ServiceDTO actual = ioServiceMapper.mapService(serviceModel);

        assertNotNull(actual);
    }
}
