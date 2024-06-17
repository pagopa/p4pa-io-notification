package it.gov.pagopa.payhub.ionotification.repository;

import it.gov.pagopa.payhub.ionotification.model.IOService;

public interface IOServiceRepositoryExt {

    IOService createIfNotExists(IOService service);

    void updateService(IOService service, String serviceId);
}
