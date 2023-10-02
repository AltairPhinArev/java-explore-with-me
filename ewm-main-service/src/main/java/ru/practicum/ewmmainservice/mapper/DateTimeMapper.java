package ru.practicum.ewmmainservice.mapper;

import java.time.LocalDateTime;

public class DateTimeMapper {

    public static LocalDateTime toLocalDateTime(String stringDate) {
        return LocalDateTime.parse(stringDate);
    }

    public static String toStringDate(LocalDateTime date) {
        return date.toString();
    }

}
