package it.gov.pagopa.payhub.ionotification.repository;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface IOServiceRepository extends MongoRepository<IOService, String>, IOServiceRepositoryExt {

    Optional<IOService> findByEnteIdAndTipoDovutoId(Long enteId, Long tipoDovutoId);
    Optional<IOService> findByServiceId(String serviceId);
}
