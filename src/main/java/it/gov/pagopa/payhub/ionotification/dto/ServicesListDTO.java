package it.gov.pagopa.payhub.ionotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicesListDTO {

    @JsonProperty("value")
    private List<ServicePaginatedResponseDTO> serviceList;
    private PaginationDTO pagination;
}
