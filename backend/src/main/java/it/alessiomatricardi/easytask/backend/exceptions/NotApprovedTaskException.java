package it.alessiomatricardi.easytask.backend.exceptions;

public class NotApprovedTaskException extends RuntimeException {

    public NotApprovedTaskException(long id) {
        super("Task " + id + " has not been approved (or not yet), you can't manage it");
    }

}
