package it.gov.pagopa.payhub.ionotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "it.gov.pagopa.payhub.ionotification.repository")
public class IONotificationApplication {


	public static void main(String[] args) {
		SpringApplication.run(IONotificationApplication.class, args);
	}

}
