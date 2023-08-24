package it.alessiomatricardi.easytask.backend.exceptions;

public class ClosedProjectException extends RuntimeException {

    public ClosedProjectException(long id) {
        super("Project " + id + " is closed, you can't manage it");
    }

}
