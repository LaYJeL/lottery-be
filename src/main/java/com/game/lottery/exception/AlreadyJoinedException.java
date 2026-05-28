package com.game.lottery.exception;

/**
 * Thrown when a user attempts to join a competition they have already joined.
 */
public class AlreadyJoinedException extends RuntimeException {
    public AlreadyJoinedException(String message) {
        super(message);
    }
}
