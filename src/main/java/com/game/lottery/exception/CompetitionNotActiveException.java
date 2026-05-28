package com.game.lottery.exception;

/**
 * Thrown when a user attempts to join a competition that is not active.
 */
public class CompetitionNotActiveException extends RuntimeException {
    public CompetitionNotActiveException(String message) {
        super(message);
    }
}
