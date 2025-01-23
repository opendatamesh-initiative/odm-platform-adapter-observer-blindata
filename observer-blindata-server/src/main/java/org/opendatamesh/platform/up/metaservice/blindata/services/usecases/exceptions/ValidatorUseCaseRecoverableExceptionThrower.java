package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ValidatorUseCaseRecoverableExceptionThrower implements UseCaseRecoverableExceptionThrower {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<UseCaseRecoverableException> exceptions = new ArrayList<>();

    @Override
    public void _throw(UseCaseRecoverableException e) {
        exceptions.add(e);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public List<UseCaseRecoverableException> getExceptions() {
        return exceptions;
    }
}
