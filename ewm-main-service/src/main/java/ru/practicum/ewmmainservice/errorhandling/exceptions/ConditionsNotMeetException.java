package ru.practicum.ewmmainservice.errorhandling.exceptions;

public class ConditionsNotMeetException extends RuntimeException {

    public ConditionsNotMeetException(String message) {
        super(message);
    }

    public ConditionsNotMeetException(String message, Throwable cause) {
        super(message, cause);
    }
}
