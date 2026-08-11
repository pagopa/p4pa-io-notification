package it.gov.pagopa.payhub.ionotification.exception;

import it.gov.pagopa.payhub.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.ionotification.exception.common.CommonExceptionHandler;
import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class IONotificationExceptionHandler extends CommonExceptionHandler {

    @ExceptionHandler({
            RetrieveServicesInvocationException.class,
            CreateServiceInvocationException.class,
            DeleteServiceInvocationException.class,
            SendNotificationInvocationException.class,
            RetrieveSenderProfileInvocationException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleFeignClientException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_GENERIC_ERROR, false);
    }

    @ExceptionHandler(IOWrongPayloadException.class)
    public ResponseEntity<IoNotificationErrorDTO> handleWrongPayloadException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_WRONG_PAYLOAD);
    }

    @ExceptionHandler({ServiceAlreadyDeletedException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleServiceAlreadyDeletedException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.FORBIDDEN, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_SERVICE_ALREADY_DELETED);
    }

    @ExceptionHandler({ServiceNotFoundException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleNotFoundException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.NOT_FOUND, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_SERVICE_NOT_FOUND);
    }

}
