package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUseCaseRecoverableExceptionHandler implements UseCaseRecoverableExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void warn(UseCaseRecoverableException e) {
        logger.warn(e.getMessage());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
