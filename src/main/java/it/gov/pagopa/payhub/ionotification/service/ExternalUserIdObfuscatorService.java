package it.gov.pagopa.payhub.ionotification.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ExternalUserIdObfuscatorService {

  private final DataCipherService dataCipherService;

  public ExternalUserIdObfuscatorService(DataCipherService dataCipherService) {
    this.dataCipherService = dataCipherService;
  }

  public String obfuscate(String externalUserId) {
    return Base64.getEncoder().encodeToString(dataCipherService.encrypt(externalUserId));
  }
}
