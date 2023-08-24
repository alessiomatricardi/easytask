package it.alessiomatricardi.easytask.backend.exceptions;

import lombok.AllArgsConstructor;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee with id " + id + " cannot be found");
    }

    public EmployeeNotFoundException(String email) {
        super("Employee with email " + email + " cannot be found");
    }

}
