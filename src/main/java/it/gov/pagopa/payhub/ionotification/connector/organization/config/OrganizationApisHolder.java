package it.gov.pagopa.payhub.ionotification.connector.organization.config;

import it.gov.pagopa.payhub.ionotification.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import it.gov.pagopa.pu.organization.generated.ApiClient;
import it.gov.pagopa.pu.organization.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Lazy
@Service
public class OrganizationApisHolder {
    private final OrganizationApi organizationApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public OrganizationApisHolder(
        OrganizationApiClientConfig clientConfig,
        RestTemplateBuilder restTemplateBuilder,
        JsonMapper jsonMapper
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "ORGANIZATION", clientConfig.isPrintBodyWhenError(),
                OrganizationErrorDTO.class, OrganizationErrorDTO::getCode, OrganizationErrorDTO::getMessage)
        );

        this.organizationApi = new OrganizationApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /**
     * It will return a {@link OrganizationApi} instrumented with the provided accessToken. Use null if auth is not required.
     */
    public OrganizationApi getOrganizationApi(String accessToken){
        return getApi(accessToken, organizationApi);
    }


    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
