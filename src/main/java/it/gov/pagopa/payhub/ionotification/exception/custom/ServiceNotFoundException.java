package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.utils.ErrorCodeConstants;

public class ServiceNotFoundException extends BaseBusinessException {

    public ServiceNotFoundException(String message){
        super(ErrorCodeConstants.ERROR_CODE_IO_NOTIFICATION_SERVICE_NOT_FOUND, message);
    }
}
