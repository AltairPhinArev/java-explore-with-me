package ru.practicum.ewmmainservice.service;

import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long userId);

    List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);
}
