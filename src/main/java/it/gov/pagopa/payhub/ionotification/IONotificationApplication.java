package it.gov.pagopa.payhub.ionotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class IONotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IONotificationApplication.class, args);
	}

}
