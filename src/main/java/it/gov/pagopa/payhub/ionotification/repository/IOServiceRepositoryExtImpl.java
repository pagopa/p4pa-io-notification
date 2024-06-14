package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.MongoException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class IOServiceRepositoryExtImpl implements IOServiceRepositoryExt{

    private final MongoTemplate mongoTemplate;

    public IOServiceRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public IOService createIfNotExists(IOService service) {
        boolean exists = mongoTemplate.exists(
                Query.query(Criteria.where("enteId").is(service.getEnteId())
                .and("tipoDovutoId").is(service.getTipoDovutoId())),
                IOService.class);

        if(!exists) {
            mongoTemplate.save(service);
            return service;
        } else {
            throw new MongoException("The service exists already");
        }
    }
}
