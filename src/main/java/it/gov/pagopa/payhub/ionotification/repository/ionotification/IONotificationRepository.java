package it.gov.pagopa.payhub.ionotification.repository.ionotification;

import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IONotificationRepository extends MongoRepository<IONotification, String> {

}
