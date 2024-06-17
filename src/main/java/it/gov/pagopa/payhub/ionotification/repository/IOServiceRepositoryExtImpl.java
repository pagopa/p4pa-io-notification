package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.MongoException;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.model.IOService.Fields;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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

    @Override
    public void updateService(IOService service, String serviceId) {
        mongoTemplate.updateFirst(Query.query(Criteria.where(Fields.enteId).is(service.getEnteId())
                .and(Fields.tipoDovutoId).is(service.getTipoDovutoId())),
                new Update()
                        .set(Fields.serviceId, serviceId)
                        .set(Fields.tipoDovutoId, service.getTipoDovutoId())
                        .set(Fields.organizationDepartmentName, service.getOrganizationDepartmentName())
                        .set(Fields.organizationFiscalCode, service.getOrganizationFiscalCode())
                        .set(Fields.organizationName, service.getOrganizationName())
                        .set(Fields.serviceDescription, service.getServiceDescription())
                        .set(Fields.creationServiceDate, LocalDateTime.now())
                ,IOService.class);
    }
}
