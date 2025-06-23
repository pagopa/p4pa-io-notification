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

import static it.gov.pagopa.payhub.ionotification.enums.ServiceStatus.CREATED;
import static it.gov.pagopa.payhub.ionotification.enums.ServiceStatus.REQUESTED;

@Repository
public class IOServiceRepositoryExtImpl implements IOServiceRepositoryExt{

    private final MongoTemplate mongoTemplate;

    public IOServiceRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UpdateResult createIfNotExists(IOService service) {
        return mongoTemplate.upsert(
                Query.query(Criteria.where(Fields.organizationId).is(service.getOrganizationId())
                        .and(Fields.debtPositionTypeOrgId).is(service.getDebtPositionTypeOrgId())),
                new Update()
                        .setOnInsert(Fields.organizationId, service.getOrganizationId())
                        .setOnInsert(Fields.status, REQUESTED)
                        .setOnInsert(Fields.debtPositionTypeOrgId, service.getDebtPositionTypeOrgId())
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
        mongoTemplate.updateFirst(Query.query(Criteria.where(Fields.organizationId).is(service.getOrganizationId())
                .and(Fields.debtPositionTypeOrgId).is(service.getDebtPositionTypeOrgId())),
                new Update()
                        .set(Fields.serviceId, serviceId)
                        .set(Fields.organizationId, service.getOrganizationId())
                        .set(Fields.status, CREATED)
                        .set(Fields.debtPositionTypeOrgId, service.getDebtPositionTypeOrgId())
                        .set(Fields.serviceName, service.getServiceName())
                        .set(Fields.serviceDescription, service.getServiceDescription())
                        .set(Fields.organizationDepartmentName, service.getOrganizationDepartmentName())
                        .set(Fields.organizationFiscalCode, service.getOrganizationFiscalCode())
                        .set(Fields.organizationName, service.getOrganizationName())
                        .set(Fields.creationServiceDate, LocalDateTime.now())
                ,IOService.class);
    }
}
