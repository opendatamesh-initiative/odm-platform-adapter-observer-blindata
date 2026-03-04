package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions;

public class UseCaseInitException extends UseCaseException {

    /**
     * Creates an exception with a message that includes the cause's detail, or the cause's class name when the cause
     * has no message (e.g. NullPointerException), so logs are always meaningful.
     */
    public UseCaseInitException(String messagePrefix, Exception cause) {
        super(buildMessage(messagePrefix, cause), cause);
    }

    private static String buildMessage(String prefix, Throwable cause) {
        if (cause == null) {
            return prefix;
        }
        String causeDetail = cause.getMessage();
        if (causeDetail != null && !causeDetail.isEmpty()) {
            return prefix + " " + causeDetail;
        }
        return prefix + " Cause: " + cause.getClass().getSimpleName();
    }

    public UseCaseInitException(String message) {
        super(message);
    }
}
