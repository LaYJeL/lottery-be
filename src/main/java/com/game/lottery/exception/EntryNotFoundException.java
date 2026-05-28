package com.game.lottery.exception;

/**
 * Thrown when a competition entry is not found.
 */
public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(String message) {
        super(message);
    }
}
