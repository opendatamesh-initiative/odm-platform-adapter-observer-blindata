package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions;

import org.springframework.http.HttpStatus;

public class OdmPlatformInternalServerException extends OdmPlatformApiException {
    public OdmPlatformInternalServerException() {
    }

    public OdmPlatformInternalServerException(String message) {
        super(message);
    }

    public OdmPlatformInternalServerException(OdmPlatformApiStandardErrors error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(error, message, cause, enableSuppression, writableStackTrace);
    }

    public OdmPlatformInternalServerException(OdmPlatformApiStandardErrors error, String message, Throwable cause) {
        super(error, message, cause);
    }

    public OdmPlatformInternalServerException(OdmPlatformApiStandardErrors error, String message) {
        super(error, message);
    }

    public OdmPlatformInternalServerException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
