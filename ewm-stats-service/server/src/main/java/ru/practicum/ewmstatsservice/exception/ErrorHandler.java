package ru.practicum.ewmstatsservice.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleValidationException(final ValidationException ex) {
        log.error(HttpStatus.BAD_REQUEST.toString(), ex);
        return new ErrorResponse(ex.getMessage());
    }
}
