package it.alessiomatricardi.easytask.backend.exceptions;

public class PasswordsMismatchException extends RuntimeException {

    public PasswordsMismatchException() {
        super("The new password and repeated new password don't match");
    }

}
