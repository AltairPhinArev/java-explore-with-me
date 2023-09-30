package ru.practicum.ewmmainservice.mapper;

import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.Request;
import ru.practicum.ewmmainservice.model.User;

import java.time.LocalDateTime;

import static ru.practicum.ewmmainservice.dto.Status.CONFIRMED;
import static ru.practicum.ewmmainservice.dto.Status.PENDING;

public class RequestMapper {


    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration() ? PENDING : CONFIRMED)
                .build();
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request entity) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .requester(entity.getRequester().getId())
                .created(entity.getCreated())
                .event(entity.getEvent().getId())
                .status(entity.getStatus())
                .build();
    }

}
