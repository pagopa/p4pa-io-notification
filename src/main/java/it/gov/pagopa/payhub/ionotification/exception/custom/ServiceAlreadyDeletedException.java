package it.gov.pagopa.payhub.ionotification.exception.custom;

public class ServiceAlreadyDeletedException extends RuntimeException {

    public ServiceAlreadyDeletedException(String message){
        super(message);
    }
}
