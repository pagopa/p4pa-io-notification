package it.gov.pagopa.payhub.ionotification.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilitiesTest {

  @Test
  void testGetTraceId(){
    // Given
    String expectedResult = "TRACEID";
    setTraceId(expectedResult);

    // When
    String result = Utilities.getTraceId();

    // Then
    Assertions.assertSame(expectedResult, result);
    clearTraceIdContext();
  }

  public static void setTraceId(String traceId) {
    MDC.put("traceId", traceId);
  }
  public static void clearTraceIdContext(){
    MDC.clear();
  }

    @ParameterizedTest
    @ValueSource(strings = {
            "AAAAAA00A00A000A", // regexp relaxed in order to match also this, useful to test with IO notification
            "RSSMRA80A01H501U",
            "BNCFNC85T10A123Z",
            "MRALRA90P41H501M",
            "LTZPNZ01T01A123B"
    })
    void givenValidFiscalCodeWhenCheckThenReturnTrue(String fiscalCode) {
        assertTrue(Utilities.checkFiscalCode(fiscalCode));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",        // P.IVA
            "RSSMRA80A01H501",    // Too short
            "RSSMRA80A01H501U12", // Too long
            "RSSMRA80A01H501!",   // Special Character
            "INVALID-CODE-XXX",   // Invalid Format
            "16caratteri_test"    // 16 character but not CF
    })
    void givenInvalidFiscalCodeWhenCheckThenReturnFalse(String fiscalCode) {
        assertFalse(Utilities.checkFiscalCode(fiscalCode));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void givenNullOrEmptyWhenCheckThenReturnFalse(String fiscalCode) {
        assertFalse(Utilities.checkFiscalCode(fiscalCode));
    }
}
