package it.gov.pagopa.payhub.ionotification.exception;

import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import it.gov.pagopa.payhub.model.generated.IoNotificationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IONotificationExceptionHandler {

    private static final Pattern RETRY_AFTER_MS_PATTERN = Pattern.compile("RetryAfterMs=(\\d+)");

    @ExceptionHandler({
            RetrieveServicesInvocationException.class,
            CreateServiceInvocationException.class,
            DeleteServiceInvocationException.class,
            SendNotificationInvocationException.class,
            RetrieveSenderProfileInvocationException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleFeignClientException(RuntimeException ex, HttpServletRequest request) {
        return handleIONotificationErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, IoNotificationErrorDTO.CodeEnum.GENERIC_ERROR);
    }

    @ExceptionHandler({IOWrongPayloadException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleWrongPayloadException(RuntimeException ex, HttpServletRequest request) {
        return handleIONotificationErrorException(ex, request, HttpStatus.BAD_REQUEST, IoNotificationErrorDTO.CodeEnum.WRONG_PAYLOAD);
    }

    @ExceptionHandler({ServiceAlreadyDeletedException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleServiceAlreadyDeletedException(RuntimeException ex, HttpServletRequest request) {
        return handleIONotificationErrorException(ex, request, HttpStatus.FORBIDDEN, IoNotificationErrorDTO.CodeEnum.SERVICE_ALREADY_DELETED);
    }

    @ExceptionHandler({ServiceNotFoundException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleNotFoundException(RuntimeException ex, HttpServletRequest request) {
        return handleIONotificationErrorException(ex, request, HttpStatus.NOT_FOUND, IoNotificationErrorDTO.CodeEnum.SERVICE_NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<IoNotificationErrorDTO> handleDataAccessException(
            DataAccessException ex, HttpServletRequest request) {

        if (isRequestRateTooLargeException(ex)) {
            Long retryAfterMs = getRetryAfterMs(ex);
            return handleRequestRateTooLargeException(ex, request, retryAfterMs);
        } else {
            return handleIONotificationErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, IoNotificationErrorDTO.CodeEnum.GENERIC_ERROR);
        }
    }

    private ResponseEntity<IoNotificationErrorDTO> handleIONotificationErrorException(
            RuntimeException ex, HttpServletRequest request, HttpStatus httpStatus, IoNotificationErrorDTO.CodeEnum errorEnum) {
        String message = ex.getMessage();
        log.info("A {} occurred handling request {}: HttpStatus {} - {}",
                ex.getClass(),
                getRequestDetails(request),
                httpStatus.value(),
                message);

        return ResponseEntity
                .status(httpStatus)
                .body(new IoNotificationErrorDTO(errorEnum, message));
    }

    private String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

    private ResponseEntity<IoNotificationErrorDTO> handleRequestRateTooLargeException(Exception ex, HttpServletRequest request, Long retryAfterMs) {
        String message = ex.getMessage();

        log.info(
                "A MongoQueryException (RequestRateTooLarge) occurred handling request {}: HttpStatus 429 - {}",
                getRequestDetails(request), message);
        log.debug("Something went wrong while accessing MongoDB", ex);

        final ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON);

        if (retryAfterMs != null) {
            long retryAfter = (long) Math.ceil((double) retryAfterMs / 1000);
            bodyBuilder.header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter))
                    .header("Retry-After-Ms", String.valueOf(retryAfterMs));
        }

        return bodyBuilder
                .body(new IoNotificationErrorDTO(IoNotificationErrorDTO.CodeEnum.TOO_MANY_REQUESTS, "Too Many Requests"));
    }

    public static Long getRetryAfterMs(DataAccessException ex) {
        Matcher matcher = RETRY_AFTER_MS_PATTERN.matcher(ex.getMessage());
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    public static boolean isRequestRateTooLargeException(DataAccessException ex) {
        return ex.getMessage().contains("TooManyRequests") || ex.getMessage().contains("Error=16500,");
    }
}
