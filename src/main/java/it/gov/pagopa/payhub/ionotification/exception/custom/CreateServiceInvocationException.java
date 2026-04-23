package it.gov.pagopa.payhub.ionotification.exception.custom;

public class CreateServiceInvocationException extends BaseBusinessException{

    public CreateServiceInvocationException(String code, String message){
        super(code, message);
    }
}
