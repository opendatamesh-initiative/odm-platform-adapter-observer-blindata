package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseException extends Exception {
    public UseCaseException(String message) {
        super(message);
    }

    public UseCaseException(String message, Exception e) {
        super(message, e);
    }

}
