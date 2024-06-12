package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IORestConnectorImpl implements IORestConnector{

    private final String subscriptionKey;
    private final IOFeignRestClient ioFeignRestClient;

    public IORestConnectorImpl(
            @Value("${rest-client.backend-io-manage.service.subscriptionKey}") String subscriptionKey, IOFeignRestClient ioFeignRestClient) {
        this.subscriptionKey = subscriptionKey;
        this.ioFeignRestClient = ioFeignRestClient;
    }
    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) {
        return ioFeignRestClient.createService(serviceRequestDTO, subscriptionKey);
    }

    @Override
    public KeysDTO getServiceKeys(String serviceId) {
        return ioFeignRestClient.getServiceKeys(serviceId, subscriptionKey);
    }

    @Override
    public ProfileResource getProfile(FiscalCodeDTO fiscalCode, String primaryKey) {
        return ioFeignRestClient.getProfile(fiscalCode, primaryKey);
    }

    @Override
    public NotificationResource sendNotification(NotificationDTO notificationDTO, String primaryKey) {
        return ioFeignRestClient.sendNotification(notificationDTO, primaryKey);
    }

    @Override
    public ServicesListDTO getAllServices(Integer limit, Integer offset) {
        return ioFeignRestClient.getAllServices(limit, offset, subscriptionKey);
    }
}
