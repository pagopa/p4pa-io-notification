package it.gov.pagopa.payhub.ionotification.exception.transcoder.handler;

import it.gov.pagopa.payhub.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;

public class DataIntegrityViolationExceptionMessageTranscoder implements ExceptionMessageTranscoder<DataIntegrityViolationException> {

  @Override
  public ExceptionMessageTranscoded transcode(DataIntegrityViolationException dataIntegrityViolationException) {
    String errorMsg = "Conflict.";
    if(dataIntegrityViolationException.getCause() instanceof MongoDataIntegrityViolationException mongoDataIntegrityViolationException) {
      errorMsg += " " + mongoDataIntegrityViolationException.getMostSpecificCause().getMessage();
    }
    return new ExceptionMessageTranscoded(
      IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_CONFLICT.getValue(),
      errorMsg,
      null) ;
  }
}
