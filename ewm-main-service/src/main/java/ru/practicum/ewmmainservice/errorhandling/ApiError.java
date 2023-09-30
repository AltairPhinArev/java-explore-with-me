package ru.practicum.ewmmainservice.errorhandling;

import lombok.Getter;
import ru.practicum.ewmmainservice.mapper.DateTimeMapper;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    String status;

    String reason;

    String message;

    String timestamp;

    public ApiError(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = DateTimeMapper.toStringDate(LocalDateTime.now());
    }
}