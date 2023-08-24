package it.alessiomatricardi.easytask.backend.exceptions;

public class SelfInsertionException extends RuntimeException {

    public SelfInsertionException(String where) {
        super("You are trying to add yourself to the " + where);
    }

}
