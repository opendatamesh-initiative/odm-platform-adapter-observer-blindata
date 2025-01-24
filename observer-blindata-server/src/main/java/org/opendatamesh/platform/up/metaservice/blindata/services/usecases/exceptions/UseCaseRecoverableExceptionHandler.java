package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;

public interface UseCaseRecoverableExceptionHandler {
    void warn(UseCaseRecoverableException e);

    Logger getLogger();

    void setLogger(Logger logger);
}
