package it.gov.pagopa.payhub.ionotification.exception.custom;

import it.gov.pagopa.payhub.ionotification.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.ionotification.utils.ErrorCodeConstants;

public class IOWrongPayloadException extends BaseBusinessException {

    public IOWrongPayloadException(String message){
        super(ErrorCodeConstants.ERROR_CODE_IO_NOTIFICATION_WRONG_PAYLOAD, message);
    }
}
