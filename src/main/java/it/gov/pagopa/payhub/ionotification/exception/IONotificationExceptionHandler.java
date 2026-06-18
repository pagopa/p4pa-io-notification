package it.gov.pagopa.payhub.ionotification.exception;

import it.gov.pagopa.payhub.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import it.gov.pagopa.payhub.ionotification.utils.Utilities;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.slf4j.event.Level;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class IONotificationExceptionHandler {

    private static final String ERROR_MESSAGE_FORMAT = "[%s] %s";

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

    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST);
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

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<IoNotificationErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_GENERIC_ERROR);
    }

    static ResponseEntity<IoNotificationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, IoNotificationErrorDTO.CategoryEnum errorEnum) {
        boolean printStackTrace = httpStatus.is5xxServerError();
        return handleException(ex, request, httpStatus, errorEnum, printStackTrace);
    }

    static ResponseEntity<IoNotificationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, IoNotificationErrorDTO.CategoryEnum errorEnum, boolean printStackTrace) {
        logException(ex, request, httpStatus, printStackTrace);

        Pair<String, String> code2message = buildReturnedMessage(ex);

        String code = Objects.requireNonNullElse(code2message.getLeft(), errorEnum.getValue());
        String message = code2message.getRight();

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new IoNotificationErrorDTO(errorEnum, code, String.format(ERROR_MESSAGE_FORMAT, code, message), Utilities.getTraceId()));
    }

    private static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, boolean printStackTrace) {
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

    private static Pair<String, String> buildReturnedMessage(Exception ex) {
        switch (ex) {
            case HttpMessageNotReadableException httpMessageNotReadableException -> {
                String errorMsg = "Required request body is missing";
                if (httpMessageNotReadableException.getCause() instanceof DatabindException jsonMappingException) {
                    errorMsg = "Cannot parse body. " +
                            jsonMappingException.getPath().stream()
                                    .map(JacksonException.Reference::getPropertyName)
                                    .collect(Collectors.joining(".")) +
                            ": " + jsonMappingException.getOriginalMessage();
                } else if (httpMessageNotReadableException.getCause() instanceof JacksonException jacksonException) {
                    errorMsg = "Cannot parse body. " + jacksonException.getOriginalMessage();
                }
                return Pair.of(IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST.name(), errorMsg);
            }
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                return Pair.of(IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST.name(),
                        "Invalid request content." +
                        methodArgumentNotValidException.getBindingResult()
                                .getAllErrors().stream()
                                .map(e -> " " +
                                        (e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName()) +
                                        ": " + e.getDefaultMessage())
                                .sorted()
                                .collect(Collectors.joining(";")));
            }
            case ConstraintViolationException constraintViolationException -> {
                return Pair.of(IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_BAD_REQUEST.name(),
                        "Invalid request content." +
                        constraintViolationException.getConstraintViolations()
                                .stream()
                                .map(e -> " " + e.getPropertyPath() + ": " + e.getMessage())
                                .sorted()
                                .collect(Collectors.joining(";")));
            }
            case HttpClientErrorException.TooManyRequests tooManyRequestsException -> {
                return Pair.of(IoNotificationErrorDTO.CategoryEnum.IO_NOTIFICATION_TOO_MANY_REQUESTS.name(), tooManyRequestsException.getMessage());
            }
            case BaseBusinessException businessException -> {
                return Pair.of(businessException.getCode(), businessException.getMessage());
            }
            default -> {
                if (ex.getCause() instanceof HttpHostConnectException) {
                    return Pair.of("CONNECTION_ERROR", ex.getMessage());
                }
                return Pair.of(null, ex.getMessage());
            }
        }
    }

    static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
