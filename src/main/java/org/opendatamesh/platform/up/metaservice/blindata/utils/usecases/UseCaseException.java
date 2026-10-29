package org.opendatamesh.platform.up.metaservice.blindata.utils.usecases;

/**
 * A business failure raised by a use case. It carries a reason, not an HTTP status: the use case hands it
 * to {@link UseCasePresenter#presentException(Exception)}, which turns it into a service API exception.
 */
public class UseCaseException extends RuntimeException {

    public enum Reason {
        /** The command breaks a business rule or is incomplete. */
        INVALID,
        /** Something the command refers to does not exist. */
        NOT_FOUND,
        /** The command clashes with the current state. */
        CONFLICT
    }

    private final Reason reason;

    public UseCaseException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public UseCaseException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
