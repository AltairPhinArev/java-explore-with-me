package ru.practicum.ewmmainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmmainservice.dto.comment.CommentShortDto;
import ru.practicum.ewmmainservice.model.Location;
import ru.practicum.ewmmainservice.dto.enums.State;
import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Long views;

    private Long confirmedRequests;

    private String description;

    private Long participantLimit;

    private State state;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private Location location;

    private Boolean requestModeration;

    private List<CommentShortDto> comments;
}