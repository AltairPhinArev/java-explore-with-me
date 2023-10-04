package ru.practicum.ewmmainservice.dto.comment;

import lombok.*;
import ru.practicum.ewmmainservice.dto.event.EventDto;
import ru.practicum.ewmmainservice.dto.event.EventShortDto;
import ru.practicum.ewmmainservice.dto.user.UserDto;
import ru.practicum.ewmmainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    Long id;

    String description;

    LocalDateTime createdOn;

    UserShortDto user;

    EventShortDto event;
}