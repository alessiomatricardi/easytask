package it.alessiomatricardi.easytask.backend.exceptions;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id) {
        super("Project with id " + id + " cannot be found");
    }

}
