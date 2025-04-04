package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions;

import org.springframework.http.HttpStatus;

//PAY ATTENTION, COPIED FROM ODM-PLATFORM!!!
public abstract class OdmPlatformApiException extends RuntimeException {

    OdmPlatformApiStandardErrors error;

    public OdmPlatformApiException() {
        super();
    }

    public OdmPlatformApiException(String message) {
        super(message);
    }

    public OdmPlatformApiException(OdmPlatformApiStandardErrors error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        setError(error);
    }

    public OdmPlatformApiException(OdmPlatformApiStandardErrors error, String message, Throwable cause) {
        super(message, cause);
        setError(error);
    }

    public OdmPlatformApiException(OdmPlatformApiStandardErrors error, String message) {
        super(message);
        setError(error);
    }

    public OdmPlatformApiException(Throwable cause) {
        super(cause);
    }

    public void setError(OdmPlatformApiStandardErrors error) {
        this.error = error;
    }

    /**
     * @return the error
     */
    public OdmPlatformApiStandardErrors getStandardError() {
        return error;
    }

    /**
     * @return the error code
     */
    public String getStandardErrorCode() {
        return error != null ? error.code() : null;
    }

    /**
     * @return the error description
     */
    public String getStandardErrorDescription() {
        return error != null ? error.description() : null;
    }


    /**
     * @return the errorName
     */
    public String getErrorName() {
        return getClass().getSimpleName();
    }

    /**
     * @return the status
     */
    public abstract HttpStatus getStatus();


}