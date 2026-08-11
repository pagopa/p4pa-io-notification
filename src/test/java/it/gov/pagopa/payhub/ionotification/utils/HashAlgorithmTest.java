package it.gov.pagopa.payhub.ionotification.utils;

import it.gov.pagopa.payhub.ionotification.exception.common.IllegalStateBusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

class HashAlgorithmTest {

  private HashAlgorithm hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getDecoder().decode("PEPPER"));

  @Test
  void givenTextWhenHashThenOk() {
    // Given
    String plain = "PLAINTEXT";

    // When
    byte[] hash = hashAlgorithm.apply(plain);

    // Then
    Assertions.assertEquals("s+QUCtO7vYNzHCDrH03EVRGPZTyfIXwBKTRrgYWqwc4=", Base64.getEncoder().encodeToString(hash));
  }

  @Test
  void givenInvalidAlgorithmWhenHashThenNoSuchAlgorithmException() {
    // Given
    hashAlgorithm = new HashAlgorithm("invalidAlgorithm", Base64.getDecoder().decode("PEPPER"));

    // When
    IllegalStateBusinessException resultException = Assertions.assertThrows(IllegalStateBusinessException.class, () -> hashAlgorithm.apply("TEXT"));

    // Then
    Assertions.assertEquals("ALGORITHM_NOT_FOUND", resultException.getCode());
    Assertions.assertEquals("Algorithm not available", resultException.getMessage());
  }
}