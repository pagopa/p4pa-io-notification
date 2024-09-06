package it.gov.pagopa.payhub.ionotification.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataCipherServiceTest {

    private final DataCipherService service = new DataCipherService("PSW");

    @Test
    void testEncrypt() {
        // Given
        String plain = "PLAINTEXT";

        // When
        byte[] cipher = service.encrypt(plain);
        String result = service.decrypt(cipher);

        // Then
        Assertions.assertEquals(plain, result);
    }
}
