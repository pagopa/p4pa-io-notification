package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.utils.HashAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DataCipherService {

  private final HashAlgorithm hashAlgorithm;

  public DataCipherService(@Value("${data-cipher.encrypt-psw}") String hashPepper) {
    hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getDecoder().decode(hashPepper));
  }

  public byte[] hash(String value) {
    return hashAlgorithm.apply(value);
  }
}
