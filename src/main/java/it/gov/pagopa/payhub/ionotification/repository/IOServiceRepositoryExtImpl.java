package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.model.IOService.Fields;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants.SERVICE_STATUS_CREATED;
import static it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants.SERVICE_STATUS_REQUESTED;

@Repository
public class IOServiceRepositoryExtImpl implements IOServiceRepositoryExt{

    private final MongoTemplate mongoTemplate;

    public IOServiceRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UpdateResult createIfNotExists(IOService service) {
        return mongoTemplate.upsert(
                Query.query(Criteria.where(Fields.enteId).is(service.getEnteId())
                        .and(Fields.tipoDovutoId).is(service.getTipoDovutoId())),
                new Update()
                        .setOnInsert(Fields.enteId, service.getEnteId())
                        .setOnInsert(Fields.status, SERVICE_STATUS_REQUESTED)
                        .setOnInsert(Fields.tipoDovutoId, service.getTipoDovutoId())
                        .setOnInsert(Fields.serviceName, service.getServiceName())
                        .setOnInsert(Fields.serviceDescription, service.getServiceDescription())
                        .setOnInsert(Fields.organizationDepartmentName, service.getOrganizationDepartmentName())
                        .setOnInsert(Fields.organizationFiscalCode, service.getOrganizationFiscalCode())
                        .setOnInsert(Fields.organizationName, service.getOrganizationName())
                        .setOnInsert(Fields.creationRequestDate, LocalDateTime.now()),
                IOService.class);
    }

    @Override
    public void updateService(IOService service, String serviceId) {
        mongoTemplate.updateFirst(Query.query(Criteria.where(Fields.enteId).is(service.getEnteId())
                .and(Fields.tipoDovutoId).is(service.getTipoDovutoId())),
                new Update()
                        .set(Fields.serviceId, serviceId)
                        .set(Fields.enteId, service.getEnteId())
                        .set(Fields.status, SERVICE_STATUS_CREATED)
                        .set(Fields.tipoDovutoId, service.getTipoDovutoId())
                        .set(Fields.serviceName, service.getServiceName())
                        .set(Fields.serviceDescription, service.getServiceDescription())
                        .set(Fields.organizationDepartmentName, service.getOrganizationDepartmentName())
                        .set(Fields.organizationFiscalCode, service.getOrganizationFiscalCode())
                        .set(Fields.organizationName, service.getOrganizationName())
                        .set(Fields.creationServiceDate, LocalDateTime.now())
                ,IOService.class);
    }
}
