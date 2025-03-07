package it.gov.pagopa.payhub.ionotification.exception;

import com.mongodb.MongoQueryException;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteError;
import org.bson.BsonDocument;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(value = {
        MongoTooManyRequestsExceptionHandler.class,
        IONotificationExceptionHandlerTest.TestController.class})
@ContextConfiguration(classes = {
        IONotificationExceptionHandlerTest.class,
        MongoTooManyRequestsExceptionHandler.class,
        IONotificationExceptionHandlerTest.TestController.class})
@AutoConfigureMockMvc(addFilters = false)
class MongoTooManyRequestsExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private IONotificationExceptionHandlerTest.TestController testControllerSpy;

    @Test
    void handleUncategorizedMongoDbException() throws Exception {

        String mongoFullErrorResponse = """
        {"ok": 0.0, "errmsg": "Error=16500, RetryAfterMs=34,\s
        Details='Response status code does not indicate success: TooManyRequests (429) Substatus: 3200 ActivityId: 46ba3855-bc3b-4670-8609-17e1c2c87778 Reason:\s
        (\\r\\nErrors : [\\r\\n \\"Request rate is large. More Request Units may be needed, so no changes were made. Please retry this request later. Learn more:
         http://aka.ms/cosmosdb-error-429\\"\\r\\n]\\r\\n) ", "code": 16500, "codeName": "RequestRateTooLarge"}
        """;

        final MongoQueryException mongoQueryException = new MongoQueryException(
                BsonDocument.parse(mongoFullErrorResponse), new ServerAddress());
        doThrow(
                new UncategorizedMongoDbException(mongoQueryException.getMessage(), mongoQueryException))
                .when(testControllerSpy).testEndpoint(eq("DATA"), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .param(IONotificationExceptionHandlerTest.DATA, "DATA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requiredField\":\"data\"}"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.RETRY_AFTER))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.RETRY_AFTER, "1"))
                .andExpect(MockMvcResultMatchers.header().string("Retry-After-Ms", "34"));
    }

    @Test
    void handleWriteDbWithoutTooManyRequestsException() throws Exception {

        String writeErrorMessage = """
            Error=16500, Substatus: 3200; ActivityId: 822d212d-5aac-4f5d-a2d4-76d6da7b619e; Reason: (
            Errors : [
              "Request rate is large. More Request Units may be needed, so no changes were made. Please retry this request later. Learn more: http://aka.ms/cosmosdb-error-429"
            ]
            );
            """;

        handleMongoWriteException(writeErrorMessage);
    }

    @Test
    void handleTooManyRequestsWriteDbException() throws Exception {

        String writeErrorMessage = """
            RetryAfterMs=34, Details='Response status code does not indicate success: TooManyRequests (429); Substatus: 3200; ActivityId: 822d212d-5aac-4f5d-a2d4-76d6da7b619e; Reason: (
            Errors : [
              "Request rate is large. More Request Units may be needed, so no changes were made. Please retry this request later. Learn more: http://aka.ms/cosmosdb-error-429"
            ]
            );
            """;

        handleMongoWriteException(writeErrorMessage);
    }

    @Test
    void handleUncategorizedMongoDbExceptionNotRequestRateTooLarge() throws Exception {

        doThrow(new UncategorizedMongoDbException("DUMMY", new Exception()))
                .when(testControllerSpy).testEndpoint(eq("DATA"), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .param(IONotificationExceptionHandlerTest.DATA, "DATA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"requiredField\":\"data\"}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"DUMMY\"}", JsonCompareMode.LENIENT));
    }


    private void handleMongoWriteException(String writeErrorMessage) throws Exception {
        final MongoWriteException mongoWriteException = new MongoWriteException(
                new WriteError(16500, writeErrorMessage, BsonDocument.parse("{}")), new ServerAddress(), Collections.emptySet());
        doThrow(
                new DataIntegrityViolationException(mongoWriteException.getMessage(), mongoWriteException))
                .when(testControllerSpy).testEndpoint(Mockito.eq("DATA"), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .param(IONotificationExceptionHandlerTest.DATA, "DATA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"requiredField\":\"data\"}"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }
}
