package it.alessiomatricardi.easytask.backend.exceptions;

public class SelfDeletionException extends RuntimeException {

    public SelfDeletionException(String where) {
            super("You are trying to remove yourself to the " + where);
        }

}
