package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.*;

public interface IORestConnector {

    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO);

    KeysDTO getServiceKeys(String serviceId);

    ProfileResource getProfile(FiscalCodeDTO fiscalCode);

    NotificationResource sendNotification(NotificationDTO notificationDTO);
}
