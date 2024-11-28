package it.gov.pagopa.payhub.ionotification.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

@ExtendWith(MockitoExtension.class)
class UserIdObfuscatorServiceTest {

    @Mock
    private DataCipherService dataCipherService = new DataCipherService("PEPPER");

    @InjectMocks
    private UserIdObfuscatorService service;

    @Test
    void whenObfuscateThenOk(){
        //Given
        String userId = "USERID";
        byte[] hashExpected = new byte[0];

        // When
        Mockito.when(dataCipherService.hash(userId)).thenReturn(hashExpected);
        String result = service.obfuscate(userId);

        // Then
        Assertions.assertEquals(Base64.getEncoder().encodeToString(hashExpected), result);
    }
}
