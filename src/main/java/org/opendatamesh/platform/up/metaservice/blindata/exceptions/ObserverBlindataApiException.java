package org.opendatamesh.platform.up.metaservice.blindata.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ObserverBlindataApiException extends RuntimeException {

    private static final long serialVersionUID = 3876573329263306459L;

    public ObserverBlindataApiException() {
        super();
    }

    public ObserverBlindataApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ObserverBlindataApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObserverBlindataApiException(String message) {
        super(message);
    }

    public ObserverBlindataApiException(Throwable cause) {
        super(cause);
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
