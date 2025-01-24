package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;

public interface UseCaseRecoverableExceptionHandler {
    void warn(String message);

    void warn(String message, Exception e);

    Logger getLogger();

    void setLogger(Logger logger);
}
