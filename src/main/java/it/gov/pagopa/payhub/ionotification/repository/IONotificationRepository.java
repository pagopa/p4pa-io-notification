package it.gov.pagopa.payhub.ionotification.repository;

import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IONotificationRepository extends MongoRepository<IONotification, String> {

    Optional<IONotification> findByUserIdAndEnteIdAndTipoDovutoId(byte[] userId, Long enteId, Long tipoDovutoId);
}
