package it.gov.pagopa.payhub.ionotification.exception;

import it.gov.pagopa.payhub.ionotification.exception.common.CommonExceptionHandlerTest;
import it.gov.pagopa.payhub.ionotification.exception.custom.IOWrongPayloadException;
import it.gov.pagopa.payhub.ionotification.exception.custom.RetrieveServicesInvocationException;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceAlreadyDeletedException;
import it.gov.pagopa.payhub.ionotification.exception.custom.ServiceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;

class IONotificationExceptionHandlerTest extends CommonExceptionHandlerTest {

    @Test
    void handleFeignClientException() throws Exception {
        doThrow(new RetrieveServicesInvocationException("ERRORCODE", "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("IO_NOTIFICATION_GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("ERRORCODE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

    @Test
    void handleWrongPayloadException() throws Exception {
        doThrow(new IOWrongPayloadException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("IO_NOTIFICATION_WRONG_PAYLOAD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("IO_NOTIFICATION_WRONG_PAYLOAD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

    @Test
    void handleServiceAlreadyDeletedException() throws Exception {
        doThrow(new ServiceAlreadyDeletedException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("IO_NOTIFICATION_SERVICE_ALREADY_DELETED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("IO_NOTIFICATION_SERVICE_ALREADY_DELETED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

    @Test
    void handleIoServiceNotFoundException() throws Exception {
        doThrow(new ServiceNotFoundException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("IO_NOTIFICATION_SERVICE_NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("IO_NOTIFICATION_SERVICE_NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

}
