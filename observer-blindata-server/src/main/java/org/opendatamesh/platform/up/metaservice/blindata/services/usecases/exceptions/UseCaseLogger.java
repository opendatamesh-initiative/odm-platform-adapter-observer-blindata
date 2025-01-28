package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public interface UseCaseLogger {
    void info(String message);

    void info(String message, Exception e);

    void warn(String message);

    void warn(String message, Exception e);
}
