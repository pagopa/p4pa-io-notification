package it.gov.pagopa.payhub.ionotification.connector;

import feign.FeignException;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IORestConnectorImpl implements IORestConnector {

    private final String subscriptionKey;
    private final IOFeignRestClient ioFeignRestClient;

    public IORestConnectorImpl(
            @Value("${rest-client.backend-io-manage.service.subscriptionKey}") String subscriptionKey,
            IOFeignRestClient ioFeignRestClient) {
        this.subscriptionKey = subscriptionKey;
        this.ioFeignRestClient = ioFeignRestClient;
    }

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) {
        try {
            return ioFeignRestClient.createService(serviceRequestDTO, subscriptionKey);
        } catch (FeignException e) {
            log.error("An error occurred while creating service: {}", e.getMessage());
            if (e.status() == 400) {
                throw new IOWrongPayloadException(String.format("There is something wrong with the payload: %s", e.getMessage()));
            }
            throw new CreateServiceInvocationException("The service was not created, please retry it");
        }
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
        try {
            return ioFeignRestClient.getAllServices(limit, offset, subscriptionKey);
        } catch (FeignException e) {
            log.error("An error occurred while retrieving all services: {}", e.getMessage());
            throw new RetrieveServicesInvocationException("It was not possible to retrieve all services from IO, please retry it");
        }
    }

    @Override
    public void deleteService(String serviceId) {
        try {
            ioFeignRestClient.deleteService(serviceId, subscriptionKey);
        } catch (FeignException e) {
            log.error("An error occurred while deleting service: {}", e.getMessage());
            if (e.status() == 404) {
                throw new ServiceNotFoundException(String.format("The service with serviceId %s does not exist", serviceId));
            }
            throw new DeleteServiceInvocationException(String.format("It was not possible to delete the service with serviceId: %s", serviceId));
        }
    }
}
