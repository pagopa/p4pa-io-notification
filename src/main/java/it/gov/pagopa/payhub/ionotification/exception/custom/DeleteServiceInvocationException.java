package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;

public class DeleteServiceInvocationException extends BaseBusinessException {

    public DeleteServiceInvocationException(String code, String message){
        super(code, message);
    }
}
