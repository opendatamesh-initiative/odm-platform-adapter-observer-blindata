package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseRecoverableExceptionContext {
    private static ThreadLocal<UseCaseRecoverableExceptionHandler> exceptionHandler =
            ThreadLocal.withInitial(DefaultUseCaseRecoverableExceptionHandler::new);

    public static UseCaseRecoverableExceptionHandler getExceptionHandler() {
        return exceptionHandler.get();
    }

    public static void setExceptionHandler(UseCaseRecoverableExceptionHandler exceptionHandler) {
        UseCaseRecoverableExceptionContext.exceptionHandler.set(exceptionHandler);
    }
}
