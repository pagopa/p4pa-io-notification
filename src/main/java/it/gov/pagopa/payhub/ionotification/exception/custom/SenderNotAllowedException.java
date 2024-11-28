package it.gov.pagopa.payhub.ionotification.exception.custom;

public class SenderNotAllowedException extends RuntimeException{

    public SenderNotAllowedException(String message){
        super(message);
    }
}
