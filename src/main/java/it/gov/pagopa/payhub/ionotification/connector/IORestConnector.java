package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;

public interface IORestConnector {

    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO);

    KeysDTO getServiceKeys(String serviceId);

    ProfileResource getProfile(FiscalCodeDTO fiscalCode, String primaryKey);

    NotificationResource sendNotification(NotificationDTO notificationDTO, String primaryKey);

    ServicesListDTO getAllServices(Integer limit, Integer offset);

    void deleteService(String serviceId);
}
