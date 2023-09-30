package ru.practicum.ewmmainservice.mapper;

import ru.practicum.ewmmainservice.dto.enums.State;
import ru.practicum.ewmmainservice.dto.event.*;
import ru.practicum.ewmmainservice.model.Location;
import ru.practicum.ewmmainservice.model.Event;

import java.time.LocalDateTime;

public class EventMapper {

    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .request(event.getRequest()  != null ? event.getRequest() : 0L)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiatorId(UserMapper.toUserDto(event.getInitiatorId()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishDate(event.getPublishDate())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event toEventFromDto(EventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .annotation(eventDto.getAnnotation())
                .category(CategoryMapper.toCategory(eventDto.getCategory()))
                .request(eventDto.getRequest())
                .createdOn(eventDto.getCreatedOn())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .initiatorId(UserMapper.toUser(eventDto.getInitiatorId()))
                .location(eventDto.getLocation())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .publishDate(eventDto.getPublishDate())
                .requestModeration(eventDto.getRequestModeration())
                .state(eventDto.getState())
                .title(eventDto.getTitle())
                .views(eventDto.getViews())
                .build();
    }

    public static Event toEventCreate(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(new Location(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static Event toEventUpdateByAdmin(UpdateEventAdminRequest dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static Event toEventUpdateByUser(UpdateEventUserRequest dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .participantLimit(dto.getParticipantLimit())
                .title(dto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getRequest())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiatorId()))
                .location(new Location(event.getLocation().getLat(), event.getLocation().getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishDate())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event entity) {
        return EventShortDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(entity.getCategory()))
                .confirmedRequests(entity.getRequest()  != null ? entity.getRequest() : 0L)
                .eventDate(entity.getEventDate())
                .initiator(UserMapper.toUserShortDto(entity.getInitiatorId()))
                .paid(entity.getPaid())
                .title(entity.getTitle())
                .views(entity.getViews())
                .build();
    }
}