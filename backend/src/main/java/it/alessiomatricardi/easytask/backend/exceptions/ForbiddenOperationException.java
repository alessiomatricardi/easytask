package it.alessiomatricardi.easytask.backend.exceptions;

public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException() {
        super("You are trying to access a forbidden operation or resource");
    }
}
