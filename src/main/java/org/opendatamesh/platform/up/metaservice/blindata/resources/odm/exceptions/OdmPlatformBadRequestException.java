package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions;

import org.springframework.http.HttpStatus;

public class OdmPlatformBadRequestException extends OdmPlatformApiException {

    public OdmPlatformBadRequestException() {
        super();
    }

    public OdmPlatformBadRequestException(OdmPlatformApiStandardErrors error, String message, Throwable cause,
                                          boolean enableSuppression, boolean writableStackTrace) {
        super(error, message, cause, enableSuppression, writableStackTrace);
    }

    public OdmPlatformBadRequestException(OdmPlatformApiStandardErrors error, String message, Throwable cause) {
        super(error, message, cause);
    }

    public OdmPlatformBadRequestException(OdmPlatformApiStandardErrors error, String message) {
        super(error, message);
    }

    public OdmPlatformBadRequestException(Throwable cause) {
        super(cause);
    }

    public OdmPlatformBadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}