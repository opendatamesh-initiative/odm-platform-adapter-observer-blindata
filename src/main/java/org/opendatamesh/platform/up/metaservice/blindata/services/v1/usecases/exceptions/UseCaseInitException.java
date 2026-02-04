package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions;

public class UseCaseInitException extends UseCaseException {
    public UseCaseInitException(String message) {
        super(message);
    }

    public UseCaseInitException(String message, Exception e) {
        super(message, e);
    }
}
