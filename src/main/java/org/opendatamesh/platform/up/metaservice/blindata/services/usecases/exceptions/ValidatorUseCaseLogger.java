package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

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
        String toAdd = message;
        if (!StringUtils.hasText(toAdd) && e != null) {
            toAdd = e.getClass().getSimpleName()
                    + (e.getMessage() != null && !e.getMessage().isBlank() ? ": " + e.getMessage() : ": No message")
                    + "\n" + getStackTraceString(e);
        }
        warnings.add(toAdd);
    }

    private static String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
