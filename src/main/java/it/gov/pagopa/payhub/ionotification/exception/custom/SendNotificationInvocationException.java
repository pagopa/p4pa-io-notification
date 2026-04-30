package it.gov.pagopa.payhub.ionotification.exception.custom;

public class SendNotificationInvocationException extends BaseBusinessException{

    public SendNotificationInvocationException(String code, String message){
        super(code, message);
    }
}
