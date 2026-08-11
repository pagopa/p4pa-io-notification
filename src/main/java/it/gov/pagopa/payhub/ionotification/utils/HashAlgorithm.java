package it.gov.pagopa.payhub.ionotification.utils;

import it.gov.pagopa.payhub.ionotification.exception.common.IllegalStateBusinessException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithm {

  private final String algorithm;
  private final byte[] pepper;

  public HashAlgorithm(String algorithm, byte[] pepper) {
    this.algorithm = algorithm;
    this.pepper = pepper;
  }

  private MessageDigest getInstance() {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateBusinessException(ErrorCodeConstants.ERROR_CODE_ALGORITHM_NOT_FOUND, "Algorithm not available", e);
    }
  }

  public byte[] apply(String s) {
    MessageDigest md = getInstance();
    md.update(s.getBytes());
    md.update(pepper);
    return md.digest();
  }

}
