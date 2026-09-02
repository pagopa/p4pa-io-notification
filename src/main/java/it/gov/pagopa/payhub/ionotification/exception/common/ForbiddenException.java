package it.gov.pagopa.payhub.ionotification.exception.common;

public class ForbiddenException extends BaseBusinessException {
  public ForbiddenException(String code, String message) {
    super(code, message);
  }
}
