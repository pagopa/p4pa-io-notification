package it.gov.pagopa.payhub.ionotification.repository;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface IOServiceRepository extends MongoRepository<IOService, String>, IOServiceRepositoryExt {
}
