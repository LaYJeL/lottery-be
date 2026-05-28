package com.game.lottery.exception;

/**
 * Thrown when a user attempts to submit an entry that has already been
 * submitted or processed.
 */
public class EntryAlreadySubmittedException extends RuntimeException {
    public EntryAlreadySubmittedException(String message) {
        super(message);
    }
}
