package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseRecoverableExceptionHandler {
    private static ThreadLocal<UseCaseRecoverableExceptionThrower> exceptionThrower =
            ThreadLocal.withInitial(DefaultUseCaseRecoverableExceptionThrower::new);

    public static UseCaseRecoverableExceptionThrower getExceptionThrower() {
        return exceptionThrower.get();
    }

    public static void setExceptionThrower(UseCaseRecoverableExceptionThrower exceptionThrower) {
        UseCaseRecoverableExceptionHandler.exceptionThrower.set(exceptionThrower);
    }
}
