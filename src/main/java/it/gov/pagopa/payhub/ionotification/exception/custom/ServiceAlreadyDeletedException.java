package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.utils.ErrorCodeConstants;

public class ServiceAlreadyDeletedException extends BaseBusinessException {

    public ServiceAlreadyDeletedException(String message){
        super(ErrorCodeConstants.ERROR_CODE_IO_NOTIFICATION_SERVICE_ALREADY_DELETED, message);
    }
}
