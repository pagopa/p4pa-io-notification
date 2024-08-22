package it.gov.pagopa.payhub.ionotification.exception.custom;

public class IOWrongPayloadException extends RuntimeException{
    public IOWrongPayloadException(String message){
        super(message);
    }
}
