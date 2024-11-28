package it.gov.pagopa.payhub.ionotification.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

class DataCipherServiceTest {

    private final DataCipherService service = new DataCipherService("PEPPER");

    @Test
    void givenTextWhenHashThenOk() {
        // Given
        String plain = "PLAINTEXT";

        // When
        byte[] hash = service.hash(plain);

        // Then
        Assertions.assertEquals("s+QUCtO7vYNzHCDrH03EVRGPZTyfIXwBKTRrgYWqwc4=", Base64.getEncoder().encodeToString(hash));
    }
}
