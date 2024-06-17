package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.MongoException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapIoService;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = IOServiceRepositoryExtImpl.class)
class IOServiceRepositoryExtTest {

    @Autowired
    IOServiceRepositoryExt serviceRepository;

    @MockBean
    MongoTemplate mongoTemplate;


    @Test
    void givenCreateIfNotExistsWhenServiceDoNotExistsTryThenSuccess() {

        when(mongoTemplate.exists(any(Query.class), Mockito.eq(IOService.class)))
                .thenReturn(false);
        IOService service = mapIoService(createServiceRequestDTO());

        when(mongoTemplate.save(any(IOService.class))).thenReturn(service);

        serviceRepository.createIfNotExists(service);

        verify(mongoTemplate, times(1)).save(service);

    }

    @Test
    void givenCreateIfNotExistsTryWhenServiceExistsThenThrowMongoException() {

        when(mongoTemplate.exists(any(Query.class), Mockito.eq(IOService.class)))
                .thenReturn(true);
        IOService service = mapIoService(createServiceRequestDTO());

        assertThrows(MongoException.class, () -> serviceRepository.createIfNotExists(service));
    }

    @Test
    void givenUpdateServiceThenSuccesses() {
        IOService service = mapIoService(createServiceRequestDTO());

        serviceRepository.updateService(service, "SERVICE_ID");

        verify(mongoTemplate, times(1)).updateFirst(
                any(Query.class),
                any(),
                eq(IOService.class)
        );
    }
}
