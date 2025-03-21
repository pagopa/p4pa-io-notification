package it.gov.pagopa.payhub.ionotification.connector.organization;

import it.gov.pagopa.payhub.ionotification.connector.organization.client.OrganizationClient;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationClient organizationClient;

    public OrganizationServiceImpl(OrganizationClient organizationClient) {
        this.organizationClient = organizationClient;
    }

    @Override
    public String getOrganizationApiKey(String accessToken, Long organizationId, OrganizationApiKeyType keyType) {
        log.info("Fetching API key for organizationId: {} and keyType: {}", organizationId, keyType);
        try {
            return organizationClient.getOrganizationApiKey(accessToken, organizationId, keyType);
        } catch (Exception e) {
            log.error("Failed to retrieve API key for organizationId: {} and keyType: {}", organizationId, keyType, e);
            return null;
        }
    }
}
