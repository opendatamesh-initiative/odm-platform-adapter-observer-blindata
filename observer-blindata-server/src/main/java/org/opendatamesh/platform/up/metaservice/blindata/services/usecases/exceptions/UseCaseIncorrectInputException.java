package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseIncorrectInputException extends RuntimeException {
    public UseCaseIncorrectInputException(String message) {
        super(message);
    }

    public UseCaseIncorrectInputException(String message, Exception e) {
        super(message, e);
    }
}
