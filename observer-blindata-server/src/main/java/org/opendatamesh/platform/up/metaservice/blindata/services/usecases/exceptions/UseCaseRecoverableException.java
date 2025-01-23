package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseRecoverableException extends RuntimeException {
    public UseCaseRecoverableException(String message) {
        super(message);
    }

    public UseCaseRecoverableException(String message, Exception e) {
        super(message, e);
    }
}
