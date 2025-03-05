package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class NotificationDTO {

    @Min(value = 3600)
    @Max(value = 604800)
    @JsonProperty("time_to_live")
    private Long timeToLive;

    @JsonProperty("fiscal_code")
    @NotNull
    @NotBlank
    private String fiscalCode;

    @NotNull private MessageContent content;

    @JsonProperty("payment_data")
    private PaymentData paymentData;
}
