package it.gov.pagopa.payhub.ionotification.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import it.gov.pagopa.payhub.ionotification.config.IORestConnectorConfig;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.exception.custom.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(
        initializers = IORestClientTest.WireMockInitializer.class,
        classes = {
                IORestConnectorImpl.class,
                IOFeignRestClient.class,
                FeignAutoConfiguration.class,
                IORestConnectorConfig.class,
                HttpMessageConvertersAutoConfiguration.class,
        })
@TestPropertySource(
        locations = "classpath:application.yml",
        properties = {
                "rest-client.backend-io-manage.service.subscriptionKey=token",
                "rest-client.backend-io-manage.service.limit=",
                "rest-client.backend-io-manage.service.offset="
        })
class IORestClientTest {

    @Autowired
    private IORestConnector ioRestConnector;

    @Test
    void givenCreateServiceThenSuccess() throws JsonProcessingException {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        wireMockServer.stubFor(post(urlEqualTo("/manage/services"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(serviceRequestDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(createServiceResponseDTO()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ServiceResponseDTO serviceResponseDTO = ioRestConnector.createService(serviceRequestDTO);

        assertNotNull(serviceResponseDTO);
    }

    @Test
    void givenCreateServiceWhenErrorFromIOThenThrowCreateServiceInvocationException() throws JsonProcessingException {

        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        wireMockServer.stubFor(post(urlEqualTo("/manage/services"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(serviceRequestDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.FORBIDDEN.value())
                )
        );

        CreateServiceInvocationException exception = assertThrows(CreateServiceInvocationException.class, () ->
                ioRestConnector.createService(serviceRequestDTO));

        assertEquals("The service was not created, please retry it", exception.getMessage());
    }

    @Test
    void givenCreateServiceWhenPayloadIsWrongThenThrowCreateServiceInvocationException() throws JsonProcessingException {

        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();

        wireMockServer.stubFor(post(urlEqualTo("/manage/services"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(serviceRequestDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        );

        assertThrows(IOWrongPayloadException.class, () ->
                ioRestConnector.createService(serviceRequestDTO));
    }


    @Test
    void givenServiceWhenGetServiceTokenThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(get(urlEqualTo("/manage/services/SERVICE_ID/keys"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getTokenIOResponse()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        KeysDTO keys = ioRestConnector.getServiceKeys("SERVICE_ID", "API_KEY");

        assertNotNull(keys);
    }

    @Test
    void givenServiceKeysWhenErrorFromIOThenThrowRetrieveServicesInvocationException() throws JsonProcessingException {

        wireMockServer.stubFor(get(urlEqualTo("/manage/services/SERVICE_ID/keys"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getTokenIOResponse()))
                        .withStatus(HttpStatus.FORBIDDEN.value())
                )
        );

        assertThrows(RetrieveServicesInvocationException.class, () ->
                ioRestConnector.getServiceKeys("SERVICE_ID", "API_KEY"));
    }

    @Test
    void givenFiscalCodeWhenGetUserProfileTokenThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(post(urlEqualTo("/profiles"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(getUserProfileRequest())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getUserProfileResponse()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ProfileResource profile = ioRestConnector.getProfile(getUserProfileRequest(), "TOKEN");

        assertNotNull(profile);
    }

    @Test
    void givenGetUserProfileWhenSenderNotAllowedTokenThenThrowSenderNotAllowedException() throws JsonProcessingException {

        FiscalCodeDTO fiscalCodeDTO = getUserProfileRequest();
        wireMockServer.stubFor(post(urlEqualTo("/profiles"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(fiscalCodeDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getUserProfileResponse()))
                        .withStatus(HttpStatus.FORBIDDEN.value())
                )
        );

        assertThrows(SenderNotAllowedException.class, () ->
                ioRestConnector.getProfile(fiscalCodeDTO, "TOKEN"));
    }

    @Test
    void givenGetUserProfileWhenErrorFromIOThenThrowRetrieveSenderProfileInvocationException() throws JsonProcessingException {

        FiscalCodeDTO fiscalCodeDTO = getUserProfileRequest();
        wireMockServer.stubFor(post(urlEqualTo("/profiles"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(fiscalCodeDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getUserProfileResponse()))
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        );

        assertThrows(RetrieveSenderProfileInvocationException.class, () ->
                ioRestConnector.getProfile(fiscalCodeDTO, "TOKEN"));
    }

    @Test
    void givenGetAllServicesThenSuccess() throws JsonProcessingException {
        wireMockServer.stubFor(get(urlEqualTo("/manage/services"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(getAllServicesResponseFeign()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ServicesListDTO services = ioRestConnector.getAllServices(null, null);

        assertNotNull(services);
    }

    @Test
    void givenGetAllServicesWhenErrorFromIOThenThrowRetrieveServicesInvocationException() {
        wireMockServer.stubFor(get(urlEqualTo("/manage/services"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.FORBIDDEN.value())
                )
        );

        assertThrows(RetrieveServicesInvocationException.class, () ->
                ioRestConnector.getAllServices(0, 20));
    }

    @Test
    void givenServiceWhenSendNotificationThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(post(urlEqualTo("/messages"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(sendNotificationRequest())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(new NotificationResource("ID")))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        NotificationResource notification = ioRestConnector.sendNotification(sendNotificationRequest(), "TOKEN");

        assertNotNull(notification);
    }

    @Test
    void givenSendNotificationWhenWrongPayloadThenThrowIOWrongPayloadException() throws JsonProcessingException {

        NotificationDTO notificationQueueDTO = sendNotificationRequest();
        wireMockServer.stubFor(post(urlEqualTo("/messages"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(notificationQueueDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(new NotificationResource("ID")))
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        );

        assertThrows(IOWrongPayloadException.class, () ->
                ioRestConnector.sendNotification(notificationQueueDTO, "TOKEN"));
    }

    @Test
    void givenSendNotificationWhenErrorFromIOThenThrowSendNotificationInvocationException() throws JsonProcessingException {

        NotificationDTO notificationQueueDTO = sendNotificationRequest();
        wireMockServer.stubFor(post(urlEqualTo("/messages"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(notificationQueueDTO)))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(new NotificationResource("ID")))
                        .withStatus(HttpStatus.FORBIDDEN.value())
                )
        );

        assertThrows(SendNotificationInvocationException.class, () ->
                ioRestConnector.sendNotification(notificationQueueDTO, "TOKEN"));
    }

    @Test
    void givenDeleteServiceThenSuccess() {

        stubDeleteService(SERVICE_ID, HttpStatus.NO_CONTENT.value());

        ioRestConnector.deleteService(SERVICE_ID);
        wireMockServer.verify(1,
                WireMock.deleteRequestedFor(WireMock.urlEqualTo("/manage/services/" + SERVICE_ID)));
    }

    @Test
    void givenDeleteServiceWhenServiceDoesNotExistsThenThrowServiceNotFoundException() {

        stubDeleteService("service_not_found", HttpStatus.NOT_FOUND.value());

        assertThrows(ServiceNotFoundException.class, () -> ioRestConnector.deleteService("service_not_found"));
    }

    @Test
    void givenDeleteServiceWhenForbiddenThenThrowDeleteServiceInvocationException() {

        stubDeleteService("service_forbidden", HttpStatus.FORBIDDEN.value());

        assertThrows(DeleteServiceInvocationException.class, () -> ioRestConnector.deleteService("service_forbidden"));
    }

    @Test
    void givenDeleteServiceWhenConflictThenThrowServiceAlreadyDeletedException() {

        stubDeleteService("service_conflict", HttpStatus.CONFLICT.value());

        assertThrows(ServiceAlreadyDeletedException.class, () -> ioRestConnector.deleteService("service_conflict"));
    }

    private static void stubDeleteService(String serviceId, int httpStatusvalue) {
        wireMockServer.stubFor(delete(urlEqualTo("/manage/services/"+ serviceId))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(httpStatusvalue)
                )
        );
    }


    private static WireMockServer wireMockServer;

    public static class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
            wireMockServer.start();

            configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

            configurableApplicationContext.addApplicationListener(
                    applicationEvent -> {
                        if (applicationEvent instanceof ContextClosedEvent) {
                            wireMockServer.stop();
                        }
                    });

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    String.format(
                            "rest-client.backend-io-manage.service.base-url=http://%s:%d",
                            wireMockServer.getOptions().bindAddress(), wireMockServer.port()));
        }
    }
}
