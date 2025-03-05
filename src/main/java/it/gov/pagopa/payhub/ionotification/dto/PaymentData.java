package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PaymentData {

    @JsonProperty("amount")
    @NotNull
    private Integer amount;

    @JsonProperty("notice_number")
    @NotNull
    private String noticeNumber;

    @JsonProperty("invalid_after_due_date")
    private Boolean invalidAfterDueDate;

    @JsonProperty("payee")
    @NotNull
    private Payee payee;
}
