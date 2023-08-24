package it.alessiomatricardi.easytask.backend.exceptions;

public class EmployeeIsNotAMemberException extends RuntimeException {

    public EmployeeIsNotAMemberException(String employeeEmail) {
        super("You can't assign a task to an employee who is not part of the project");
    }
}
