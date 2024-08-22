package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapIoService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void givenCreateIfNotExistsWhenServiceDoNotExistsThenInsert() {
        UpdateResult result = getUpdateResult(new BsonObjectId(new ObjectId()));
        assertNotNull(result.getUpsertedId());
    }

    @Test
    void givenCreateIfNotExistsWhenServiceExistsThenDoNotInsert() {
        UpdateResult result = getUpdateResult(null);
        assertNull(result.getUpsertedId());
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

    private UpdateResult getUpdateResult(BsonObjectId value) {
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getUpsertedId()).thenReturn(value);

        when(mongoTemplate.upsert(any(Query.class), any(Update.class), eq(IOService.class)))
                .thenReturn(updateResult);

        UpdateResult result = serviceRepository.createIfNotExists(mapIoService(createServiceRequestDTO()));

        verify(mongoTemplate, times(1))
                .upsert(any(Query.class), any(Update.class), eq(IOService.class));
        return result;
    }
}
