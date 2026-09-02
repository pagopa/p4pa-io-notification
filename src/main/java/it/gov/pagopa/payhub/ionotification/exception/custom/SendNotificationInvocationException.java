package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;

public class SendNotificationInvocationException extends BaseBusinessException {

    public SendNotificationInvocationException(String code, String message){
        super(code, message);
    }
}
