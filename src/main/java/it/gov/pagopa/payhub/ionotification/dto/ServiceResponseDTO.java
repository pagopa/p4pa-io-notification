package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {

    private String id;
    private Integer version;
    @JsonProperty("name")
    private String serviceName;
    private String description;
    private OrganizationDTO organization;
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
