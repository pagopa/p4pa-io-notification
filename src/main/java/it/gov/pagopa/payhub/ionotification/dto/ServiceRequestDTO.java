package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Payload used to create a IO service.
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequestDTO {
    @JsonProperty("name")
    private String serviceName;
    private String description;
    private OrganizationDTO organization;
    @JsonProperty("require_secure_channel")
    private Boolean requireSecureChannels;
    @JsonProperty("authorized_cidrs")
    private List<String> authorizedCidrs;
    @JsonProperty("authorized_recipients")
    private List<String> authorizedRecipients;
    @JsonProperty("metadata")
    private ServiceRequestMetadataDTO serviceMetadata;

}

