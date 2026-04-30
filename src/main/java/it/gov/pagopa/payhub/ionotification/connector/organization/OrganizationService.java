package it.gov.pagopa.payhub.ionotification.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;

public interface OrganizationService {

  String getOrganizationApiKey(String accessToken, Long organizationId, OrganizationApiKeyType keyType);
}
