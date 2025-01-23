package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;

public interface UseCaseRecoverableExceptionThrower {
    void _throw(UseCaseRecoverableException e);

    Logger getLogger();

    void setLogger(Logger logger);
}
