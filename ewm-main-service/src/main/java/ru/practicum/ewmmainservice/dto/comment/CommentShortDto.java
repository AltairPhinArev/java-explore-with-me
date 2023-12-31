package ru.practicum.ewmmainservice.dto.comment;

import lombok.*;
import ru.practicum.ewmmainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {

    Long id;

    UserShortDto user;

    String description;

    LocalDateTime createdOn;
}
