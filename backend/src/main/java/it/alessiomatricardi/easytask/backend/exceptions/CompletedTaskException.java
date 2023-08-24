package it.alessiomatricardi.easytask.backend.exceptions;

public class CompletedTaskException extends RuntimeException {

    public CompletedTaskException(long id) {
        super("Task " + id + " is completed, you can't manage it");
    }
}
