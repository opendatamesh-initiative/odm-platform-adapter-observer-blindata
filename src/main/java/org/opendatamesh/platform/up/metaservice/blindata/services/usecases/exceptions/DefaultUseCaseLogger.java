package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUseCaseLogger implements UseCaseLogger {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Exception e) {
        logger.info(message, e);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Exception e) {
        logger.warn(message, e);
    }
}
