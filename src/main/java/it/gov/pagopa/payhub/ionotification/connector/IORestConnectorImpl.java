package it.gov.pagopa.payhub.ionotification.connector;

import feign.FeignException;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.exception.custom.CreateServiceInvocationException;
import it.gov.pagopa.payhub.ionotification.exception.custom.IOWrongPayloadExceptiion;
import it.gov.pagopa.payhub.ionotification.exception.custom.RetrieveServicesInvocationException;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IORestConnectorImpl implements IORestConnector{

    private final String subscriptionKey;
    private final Integer limit;
    private final Integer offset;
    private final IOFeignRestClient ioFeignRestClient;

    public IORestConnectorImpl(
            @Value("${rest-client.backend-io-manage.service.subscriptionKey}") String subscriptionKey,
            @Value("${rest-client.backend-io-manage.service.limit}") Integer limit,
            @Value("${rest-client.backend-io-manage.service.offset}") Integer offset,
            IOFeignRestClient ioFeignRestClient) {
        this.subscriptionKey = subscriptionKey;
        this.limit = limit;
        this.offset = offset;
        this.ioFeignRestClient = ioFeignRestClient;
    }
    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) {
        try {
            return ioFeignRestClient.createService(serviceRequestDTO, subscriptionKey);
        } catch (FeignException e) {
            log.error("An error occurred while creating service: {}", e.getMessage(), e);
            if (e.status() == 400){
                throw new IOWrongPayloadExceptiion(String.format("There is something wrong with the payload: %s", e.getMessage()));
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
    public ServicesListDTO getAllServices() {
        try {
            return ioFeignRestClient.getAllServices(limit, offset, subscriptionKey);
        } catch (FeignException e) {
            log.error("An error occurred while retrieving all services: {}", e.getMessage(), e);
            throw new RetrieveServicesInvocationException("The service was not created, please retry it");
        }
    }
}
