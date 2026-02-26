package it.gov.pagopa.payhub.ionotification.connector.io;

import feign.FeignException;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import it.gov.pagopa.payhub.ionotification.performancelogger.PerformanceLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

@Service
@Slf4j
@CacheConfig
public class IORestConnectorImpl implements IORestConnector {

    private final String subscriptionKey;
    private final IOFeignRestClient ioFeignRestClient;

    public IORestConnectorImpl(
            @Value("${rest.backend-io-manage.service.subscriptionKey}") String subscriptionKey,
            IOFeignRestClient ioFeignRestClient) {
        this.subscriptionKey = subscriptionKey;
        this.ioFeignRestClient = ioFeignRestClient;
    }

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) {
        try {
            return execute("IO/createService", () -> ioFeignRestClient.createService(serviceRequestDTO, subscriptionKey));
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new IOWrongPayloadException(String.format("[IO_NOTIFICATION_WRONG_PAYLOAD] There is something wrong with the payload: %s", e.getMessage()));
            }
            throw new CreateServiceInvocationException("[IO_NOTIFICATION_GENERIC_ERROR] The service was not created, please retry it:" + e.getMessage());
        }
    }

    @Override
    @Cacheable(cacheNames = it.gov.pagopa.payhub.ionotification.config.CacheConfig.Fields.ioServices, key = "#serviceId")
    public KeysDTO getServiceKeys(String serviceId, String apiKey) {
        try {
            return execute("IO/getServiceKeys", () -> ioFeignRestClient.getServiceKeys(serviceId, apiKey));
        } catch (FeignException e) {
            throw new RetrieveServicesInvocationException("[IO_NOTIFICATION_GENERIC_ERROR] It was not possible to retrieve the token from IO: " +  e.getMessage());
        }
    }

    @Override
    @Cacheable(cacheNames = it.gov.pagopa.payhub.ionotification.config.CacheConfig.Fields.ioServices, key = "#fiscalCode.getFiscalCode()")
    public ProfileResource getProfile(FiscalCodeDTO fiscalCode, String primaryKey) {
        try {
            return execute("IO/getProfile", () -> ioFeignRestClient.getProfile(fiscalCode, primaryKey));
        } catch (FeignException e) {
            if (e.status() == 403) {
                return null;
            }
            throw new RetrieveSenderProfileInvocationException("[IO_NOTIFICATION_GENERIC_ERROR] It was not possible to verify if the user is allowed to receive notification:" + e.getMessage());
        }
    }

    @Override
    public NotificationResource sendNotification(NotificationDTO notificationDTO, String primaryKey) {
        try {
            return execute("IO/sendNotification", () -> ioFeignRestClient.sendNotification(notificationDTO, primaryKey));
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new IOWrongPayloadException(String.format("[IO_NOTIFICATION_WRONG_PAYLOAD] There is something wrong with the payload: %s", e.getMessage()));
            }
            throw new SendNotificationInvocationException("[IO_NOTIFICATION_GENERIC_ERROR] There was an error processing the request of notification: " + e.getMessage());
        }
    }

    @Override
    public ServicesListDTO getAllServices(Integer limit, Integer offset) {
        try {
            return execute("IO/getAllServices", () -> ioFeignRestClient.getAllServices(limit, offset, subscriptionKey));
        } catch (FeignException e) {
            throw new RetrieveServicesInvocationException("[IO_NOTIFICATION_GENERIC_ERROR] It was not possible to retrieve all services from IO, please retry it: " + e.getMessage());
        }
    }

    @Override
    public void deleteService(String serviceId) {
        try {
            execute("IO/deleteService", () -> {
                ioFeignRestClient.deleteService(serviceId, subscriptionKey);
                return serviceId;
            });
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ServiceNotFoundException(String.format("[IO_NOTIFICATION_SERVICE_NOT_FOUND] The service with serviceId %s does not exist in IO", serviceId));
            } else if (e.status() == 409) {
                throw new ServiceAlreadyDeletedException(String.format("[IO_NOTIFICATION_SERVICE_ALREADY_DELETED] The service with serviceId %s is already deleted from IO", serviceId));
            }
            throw new DeleteServiceInvocationException(String.format("[IO_NOTIFICATION_GENERIC_ERROR] It was not possible to delete the service with serviceId: %s in IO: %s", serviceId, e.getMessage()));
        }
    }

    private <T> T execute(String service, ThrowingSupplier<T> logic){
        return PerformanceLogger.execute("REST_INVOKE", service, logic, null, null);
    }
}
