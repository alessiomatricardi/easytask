package it.alessiomatricardi.easytask.backend.exceptions;

public class MemberHasAssignedTasksException extends RuntimeException {

    public MemberHasAssignedTasksException(String memberEmail) {
        super(memberEmail + " has assigned tasks which are not completed, you can't remove him");
    }
}
