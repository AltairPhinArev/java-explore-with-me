package ru.practicum.ewmmainservice.dto.event;

import lombok.*;
import ru.practicum.ewmmainservice.dto.enums.State;
import ru.practicum.ewmmainservice.model.Location;
import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.user.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private long request;

    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    private UserDto initiatorId;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private LocalDateTime publishDate;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Long views;
}