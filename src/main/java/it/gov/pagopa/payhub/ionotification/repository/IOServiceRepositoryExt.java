package it.gov.pagopa.payhub.ionotification.repository;

import com.mongodb.client.result.UpdateResult;
import it.gov.pagopa.payhub.ionotification.model.IOService;

public interface IOServiceRepositoryExt {

    UpdateResult createIfNotExists(IOService service);

    void updateService(IOService service, String serviceId);
}
