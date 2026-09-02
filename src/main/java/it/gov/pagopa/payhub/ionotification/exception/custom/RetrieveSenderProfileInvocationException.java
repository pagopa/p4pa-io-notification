package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;

public class RetrieveSenderProfileInvocationException extends BaseBusinessException {

    public RetrieveSenderProfileInvocationException(String code, String message){
        super(code, message);
    }
}
