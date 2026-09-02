package it.gov.pagopa.payhub.ionotification.config.rest;

import it.gov.pagopa.payhub.ionotification.dto.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
