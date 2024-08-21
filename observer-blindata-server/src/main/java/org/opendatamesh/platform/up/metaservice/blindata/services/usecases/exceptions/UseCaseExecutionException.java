package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseExecutionException extends UseCaseException {
    public UseCaseExecutionException(String message) {
        super(message);
    }

    public UseCaseExecutionException(String message, Exception e) {
        super(message, e);
    }
}
