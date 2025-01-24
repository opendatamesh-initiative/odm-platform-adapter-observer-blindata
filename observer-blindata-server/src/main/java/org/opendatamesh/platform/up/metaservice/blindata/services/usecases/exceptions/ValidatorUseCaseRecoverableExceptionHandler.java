package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ValidatorUseCaseRecoverableExceptionHandler implements UseCaseRecoverableExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<String> warnings = new ArrayList<>();

    @Override
    public void warn(String message) {
        warnings.add(message);
    }

    @Override
    public void warn(String message, Exception e) {
        warnings.add(message);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
