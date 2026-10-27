package org.opendatamesh.platform.up.metaservice.blindata.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends ObserverBlindataApiException {

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
