package it.alessiomatricardi.easytask.backend.exceptions;

public class EmployeeAlreadyExistsException extends RuntimeException {

    public EmployeeAlreadyExistsException(Long id) {
        super("An employee with id " + id + " already exists");
    }

    public EmployeeAlreadyExistsException(String email) {
        super("An employee with email " + email + " already exists");
    }

}
