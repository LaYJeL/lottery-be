package com.game.lottery.exception;

/**
 * Thrown when an entry is in an invalid state for the requested operation.
 */
public class InvalidEntryStateException extends RuntimeException {
    public InvalidEntryStateException(String message) {
        super(message);
    }
}
