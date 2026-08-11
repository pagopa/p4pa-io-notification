package it.gov.pagopa.payhub.ionotification.exception.common;

public class NotAuthorizedException extends BaseBusinessException {
  public NotAuthorizedException(String code, String message) {
    super(code, message);
  }
}
