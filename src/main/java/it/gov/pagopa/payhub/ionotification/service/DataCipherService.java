package it.gov.pagopa.payhub.ionotification.service;

import it.gov.pagopa.payhub.ionotification.utils.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataCipherService {

  private final String encryptPsw;

  public DataCipherService(
          @Value("${data-cipher.encrypt-psw}") String encryptPsw) {
    this.encryptPsw = encryptPsw;
  }

  public byte[] encrypt(String plainText){
    return AESUtils.encrypt(encryptPsw, plainText);
  }

  public String decrypt(byte[] cipherData){
    return AESUtils.decrypt(encryptPsw, cipherData);
  }
}
