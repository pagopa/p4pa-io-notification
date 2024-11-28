package it.gov.pagopa.payhub.ionotification.exception.custom;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message){
        super(message);
    }
}
