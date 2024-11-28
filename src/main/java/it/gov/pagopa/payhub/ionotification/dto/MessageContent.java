package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MessageContent {

    @NotNull
    @NotBlank
    @Length(min = 10, max = 120)
    private String subject;

    @NotNull
    @NotBlank
    @Length(min = 80, max = 10000)
    private String markdown;

}
