package it.gov.pagopa.payhub.ionotification.exception.common;

import it.gov.pagopa.payhub.ionotification.dto.generated.ErrorFieldDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.ionotification.exception.transcoder.ExceptionMessageTranscoderService;
import it.gov.pagopa.payhub.ionotification.utils.Utilities;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@Slf4j
public class CommonExceptionHandler {

  private static final ExceptionMessageTranscoderService exceptionMessageTranscoderService = new ExceptionMessageTranscoderService();

  //region Spring Data
  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleDataIntegrityViolationException(RuntimeException ex, HttpServletRequest request){
    return handleException(ex, request, HttpStatus.CONFLICT, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_CONFLICT);
  }

  @ExceptionHandler({CannotAcquireLockException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleCannotAcquireLockException(CannotAcquireLockException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.TOO_MANY_REQUESTS, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_TOO_MANY_REQUESTS);
  }
  //endregion

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<IoNotificationErrorDTO> handleConflictException(ConflictException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.CONFLICT, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_CONFLICT);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<IoNotificationErrorDTO> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.FORBIDDEN, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_FORBIDDEN);
  }

  @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ConversionFailedException.class, InvalidValueException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity<IoNotificationErrorDTO> handleNotAuthorizedException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.UNAUTHORIZED, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_UNAUTHORIZED);
  }

  @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
  public ResponseEntity<IoNotificationErrorDTO> handleInvokedHttpClientTooManyRequestsError(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.TOO_MANY_REQUESTS, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_TOO_MANY_REQUESTS);
  }

  @ExceptionHandler({ServletException.class, ErrorResponseException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleServletException(Exception ex, HttpServletRequest request) {
    HttpStatusCode httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    IoNotificationErrorDTO.CategoryEnum errorCode = IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_GENERIC_ERROR;
    if (ex instanceof ErrorResponse errorResponse) {
      httpStatus = errorResponse.getStatusCode();
      if (httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
        errorCode = IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_NOT_FOUND;
      } else if (httpStatus.is4xxClientError()) {
        errorCode = IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST;
      }
    }
    return handleException(ex, request, httpStatus, errorCode);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<IoNotificationErrorDTO> handleResourceNotFoundException(NotFoundException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.NOT_FOUND, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_NOT_FOUND);
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_GENERIC_ERROR);
  }

  @ExceptionHandler({AuthorizationDeniedException.class})
  public ResponseEntity<IoNotificationErrorDTO> handleAuthorizationDeniedException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.FORBIDDEN, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_FORBIDDEN);
  }

  public static ResponseEntity<IoNotificationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, IoNotificationErrorDTO.CategoryEnum errorEnum) {
    return handleException(ex, request, httpStatus, errorEnum, null);
  }
  public static ResponseEntity<IoNotificationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, IoNotificationErrorDTO.CategoryEnum errorEnum, Boolean printStackTrace) {
    logException(ex, request, httpStatus, printStackTrace);

    ExceptionMessageTranscoded code2message = buildReturnedMessage(ex);

    String code = Objects.requireNonNullElse(code2message.getCode(), errorEnum.getValue());
    String message = code2message.getMessage();
    List<ErrorFieldDTO> fields = code2message.getFields();

    return ResponseEntity
      .status(httpStatus)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new IoNotificationErrorDTO(errorEnum, code, message, fields, Utilities.getTraceId()));
  }

  public static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, Boolean overridePrintStackTrace) {
    boolean printStackTrace = Objects.requireNonNullElse(overridePrintStackTrace, httpStatus.is5xxServerError());
    Level logLevel = printStackTrace ? Level.ERROR : Level.INFO;
    log.makeLoggingEventBuilder(logLevel)
      .log("A {} occurred handling request {}: HttpStatus {} - {}",
        ex.getClass(),
        getRequestDetails(request),
        httpStatus.value(),
        ex.getMessage(),
        printStackTrace ? ex : null
      );
    if (!printStackTrace && log.isDebugEnabled() && ex.getCause() != null) {
      log.debug("CausedBy: ", ex.getCause());
    }
  }

  private static ExceptionMessageTranscoded buildReturnedMessage(Exception ex) {
    return exceptionMessageTranscoderService.transcode(ex);
  }

  public static String getRequestDetails(HttpServletRequest request) {
    String method = Objects.requireNonNullElse(request.getMethod(), "")
      .replace('\n', '_')
      .replace('\r', '_');
    String requestUri = Objects.requireNonNullElse(request.getRequestURI(), "")
      .replace('\n', '_')
      .replace('\r', '_');
    return "%s %s".formatted(method, requestUri);
  }
}
