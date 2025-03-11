package it.gov.pagopa.payhub.ionotification.config;

import it.gov.pagopa.payhub.ionotification.connector.io.IOFeignRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {IOFeignRestClient.class})
public class IORestConnectorConfig {
}
