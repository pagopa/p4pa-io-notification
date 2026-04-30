package it.gov.pagopa.payhub.ionotification.exception.custom;

public class DeleteServiceInvocationException extends BaseBusinessException{

    public DeleteServiceInvocationException(String code, String message){
        super(code, message);
    }
}
