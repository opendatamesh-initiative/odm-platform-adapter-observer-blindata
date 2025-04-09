package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseLoggerContext {
    private static ThreadLocal<UseCaseLogger> logger =
            ThreadLocal.withInitial(DefaultUseCaseLogger::new);

    public static UseCaseLogger getUseCaseLogger() {
        return logger.get();
    }

    public static void setUseCaseLogger(UseCaseLogger exceptionHandler) {
        UseCaseLoggerContext.logger.set(exceptionHandler);
    }
}
