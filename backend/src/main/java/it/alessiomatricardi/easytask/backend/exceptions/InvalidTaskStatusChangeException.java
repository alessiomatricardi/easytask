package it.alessiomatricardi.easytask.backend.exceptions;

public class InvalidTaskStatusChangeException extends RuntimeException {

    public InvalidTaskStatusChangeException(long id, String from, String to) {
        super("Task " + id + ": invalid status change " + from +" -> "+ to);
    }
}
