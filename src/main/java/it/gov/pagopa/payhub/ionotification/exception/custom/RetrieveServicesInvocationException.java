package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;

public class RetrieveServicesInvocationException extends BaseBusinessException {

    public RetrieveServicesInvocationException(String code, String message){
        super(code, message);
    }
}
