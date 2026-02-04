package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;
import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.setUseCaseLogger;

public class RunWithUseCaseLogger implements Runnable {
    private Runnable runnable;
    private UseCaseLogger logger;

    public RunWithUseCaseLogger(UseCaseLogger logger, Runnable runnable) {
        this.runnable = runnable;
        this.logger = logger;
    }

    @Override
    public void run() {
        UseCaseLogger previousLogger = getUseCaseLogger();
        try {
            setUseCaseLogger(logger);
            runnable.run();
        } finally {
            setUseCaseLogger(previousLogger);
        }
    }
}
