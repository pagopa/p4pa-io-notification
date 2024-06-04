package it.gov.pagopa.payhub.ionotification.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import it.gov.pagopa.payhub.ionotification.config.IORestConnectorConfig;
import it.gov.pagopa.payhub.ionotification.dto.*;
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

import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        properties = {"rest-client.backend-io-manage.service.subscriptionKey=token"})
class IORestClientTest {

    @Autowired
    private IORestConnector ioRestConnector;


    @Test
    void givenCreateServiceThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(post(urlEqualTo("/manage/services"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(createServiceRequestDTO())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(createServiceResponseDTO()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ServiceResponseDTO serviceResponseDTO = ioRestConnector.createService(createServiceRequestDTO());

        assertNotNull(serviceResponseDTO);
    }

    @Test
    void givenServiceWhenGetServiceTokenThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(get(urlEqualTo("/manage/services/SERVICE_ID/keys"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(createServiceIOKeys()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        KeysDTO keys = ioRestConnector.getServiceKeys("SERVICE_ID");

        assertNotNull(keys);
    }

    @Test
    void givenFiscalCodeWhenGetUserProfileTokenThenSuccess() throws JsonProcessingException {

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/profiles"))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(createUserProfile())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(createProfileResource()))
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ProfileResource profile = ioRestConnector.getProfile(createUserProfile());

        assertNotNull(profile);
    }

    private ServiceRequestDTO createServiceRequestDTO() {
        return ServiceRequestDTO.builder()
                .serviceName("SERVICE_NAME")
                .description("DESCRIPTION")
                .organization(createOrganizationDTO())
                .serviceMetadata(createServiceRequestMetadataDTO())
                .build();
    }

    private ServiceRequestMetadataDTO createServiceRequestMetadataDTO() {
        return ServiceRequestMetadataDTO.builder()
                .email("EMAIL")
                .phone("PHONE")
                .supportUrl("SUPPORT_URL")
                .privacyUrl("PRIVACY_URL")
                .tosUrl("TOS_URL")
                .scope("SCOPE")
                .topicId(0)
                .build();
    }

    private static OrganizationDTO createOrganizationDTO() {
        return OrganizationDTO.builder()
                .departmentName("PRODUCT_DEPARTMENT_NAME")
                .organizationName("ORGANIZATION_NAME")
                .organizationFiscalCode("ORGANIZATION_VAT")
                .build();
    }

    private ServiceResponseDTO createServiceResponseDTO() {
        ServiceResponseMetadataDTO serviceMetadataDTO = createServiceResponseMetadataDTO();
        return ServiceResponseDTO.builder()
                .id("SERVICE_ID")
                .serviceName("SERVICE_NAME")
                .organization(createOrganizationDTO())
                .serviceMetadata(serviceMetadataDTO)
                .build();
    }

    private ServiceResponseMetadataDTO createServiceResponseMetadataDTO() {
        return ServiceResponseMetadataDTO.builder()
                .email("EMAIL")
                .phone("PHONE")
                .supportUrl("SUPPORT_URL")
                .privacyUrl("PRIVACY_URL")
                .tosUrl("TOS_URL")
                .scope("SCOPE")
                .topic(new TopicDTO(0,"Altro"))
                .build();
    }

    private KeysDTO createServiceIOKeys() {
        return KeysDTO.builder()
                .primaryKey("PRIMARY_KEY")
                .secondaryKey("SECONDARY_KEY")
                .build();
    }

    private ProfileResource createProfileResource(){
        return ProfileResource.builder()
                .senderAllowed(true)
                .preferredLanguages(new ArrayList<>())
                .build();
    }

    private FiscalCodeDTO createUserProfile(){
        return FiscalCodeDTO.builder()
                .fiscalCode("FISCAL_CODE")
                .build();
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
