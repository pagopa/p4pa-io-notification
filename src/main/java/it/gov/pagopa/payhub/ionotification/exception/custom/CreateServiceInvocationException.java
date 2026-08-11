package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;

public class CreateServiceInvocationException extends BaseBusinessException {

    public CreateServiceInvocationException(String code, String message){
        super(code, message);
    }
}
