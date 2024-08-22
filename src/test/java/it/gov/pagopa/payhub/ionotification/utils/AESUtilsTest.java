package it.gov.pagopa.payhub.ionotification.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AESUtilsTest {

    @Test
    void test() {
        // Given
        String plain = "PLAINTEXT";
        String psw = "PSW";

        // When
        byte[] cipher = AESUtils.encrypt(psw, plain);
        String result = AESUtils.decrypt(psw, cipher);

        // Then
        Assertions.assertEquals(result, plain);
    }
}
