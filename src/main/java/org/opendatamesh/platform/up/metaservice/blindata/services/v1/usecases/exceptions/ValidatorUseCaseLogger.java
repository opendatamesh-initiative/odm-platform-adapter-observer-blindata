package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidatorUseCaseLogger implements UseCaseLogger {

    private List<String> warnings = new ArrayList<>();

    @Override
    public void info(String message) {
        //DO NOTHING
    }

    @Override
    public void info(String message, Exception e) {
        //DO NOTHING
    }

    @Override
    public void warn(String message) {
        warnings.add(message);
    }

    @Override
    public void warn(String message, Exception e) {
        warnings.add(message);
    }


    public List<String> getWarnings() {
        return warnings;
    }
}
