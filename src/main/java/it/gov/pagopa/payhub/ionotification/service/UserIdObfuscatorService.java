package it.gov.pagopa.payhub.ionotification.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UserIdObfuscatorService {

  private final DataCipherService dataCipherService;

  public UserIdObfuscatorService(DataCipherService dataCipherService) {
    this.dataCipherService = dataCipherService;
  }

  public String obfuscate(String externalUserId) {
    return Base64.getUrlEncoder().encodeToString(dataCipherService.hash(externalUserId));
  }
}
