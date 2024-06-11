package it.gov.pagopa.payhub.ionotification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationDTO {

    private int offset;
    private int limit;
    private int count;
}
