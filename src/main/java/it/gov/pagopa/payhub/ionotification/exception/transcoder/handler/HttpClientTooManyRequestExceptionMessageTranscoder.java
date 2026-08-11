package it.gov.pagopa.payhub.ionotification.exception.transcoder.handler;

import it.gov.pagopa.payhub.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.client.HttpClientErrorException;

public class HttpClientTooManyRequestExceptionMessageTranscoder implements ExceptionMessageTranscoder<HttpClientErrorException.TooManyRequests> {
  @Override
  public ExceptionMessageTranscoded transcode(HttpClientErrorException.TooManyRequests tooManyRequestsException) {
    return new ExceptionMessageTranscoded(
      IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_TOO_MANY_REQUESTS.getValue(),
      tooManyRequestsException.getMessage(),
      null);
  }
}
