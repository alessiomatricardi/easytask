package it.alessiomatricardi.easytask.backend.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " cannot be found");
    }

}
