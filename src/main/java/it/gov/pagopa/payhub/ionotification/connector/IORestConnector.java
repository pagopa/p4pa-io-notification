package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.KeysDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServiceRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;

public interface IORestConnector {

    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO);

    KeysDTO getServiceKeys(String serviceId);
}
