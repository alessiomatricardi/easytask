package it.alessiomatricardi.easytask.backend.exceptions;

public class ProjectAlreadyExistsException extends RuntimeException {

    public ProjectAlreadyExistsException(String name) {
        super("A project with name " + name + " already exists");
    }

}
