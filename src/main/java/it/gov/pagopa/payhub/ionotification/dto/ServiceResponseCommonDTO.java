package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ServiceResponseCommonDTO {
    private String id;
    @JsonProperty("name")
    private String serviceName;
    private String description;
    private OrganizationResponseDTO organization;
    @JsonProperty("require_secure_channel")
    private Boolean requireSecureChannels;
    @JsonProperty("authorized_recipients")
    private List<String> authorizedRecipients;
    @JsonProperty("authorized_cidrs")
    private List<String> authorizedCidrs;
    @JsonProperty("max_allowed_payment_amount")
    private Integer maxAllowedPaymentAmount;
    @JsonProperty("metadata")
    private ServiceResponseMetadataDTO serviceMetadata;
}
