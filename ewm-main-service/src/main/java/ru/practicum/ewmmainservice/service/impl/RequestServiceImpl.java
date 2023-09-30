package ru.practicum.ewmmainservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.dto.State;
import ru.practicum.ewmmainservice.dto.Status;
import ru.practicum.ewmmainservice.dto.event.EventDto;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.dto.user.UserDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;
import ru.practicum.ewmmainservice.mapper.EventMapper;
import ru.practicum.ewmmainservice.mapper.RequestMapper;
import ru.practicum.ewmmainservice.mapper.UserMapper;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.Request;
import ru.practicum.ewmmainservice.model.User;
import ru.practicum.ewmmainservice.repository.EventRepository;
import ru.practicum.ewmmainservice.repository.RequestRepository;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.RequestService;
import ru.practicum.ewmmainservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

    RequestRepository requestRepository;

    EventService eventService;

    EventRepository eventRepository;

    UserService userService;

    @Lazy
    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, EventService eventService, EventRepository eventRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (userService.checkUser(userId)) {
            return requestRepository.findAllByRequester(UserMapper.toUser(userService.getUserById(userId)))
                    .stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("User not found with id =" + userId);
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, UserMapper.toUser(userService.getUserById(userId)))) {
            throw new NotFoundException("Event not found with id =" + eventId + " and userId =" + userId);
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

         EventDto event = eventService.getEventById(eventId);
         UserDto user = userService.getUserById(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException
                    ("Request with requesterId=" + userId + " and eventId=" + eventId + " already exist");
        }
        if (userId.equals(event.getInitiatorId().getId())) {
            throw new ConflictException("User with id=" + userId + " must not be equal to initiator");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event with id="+ eventId +" is not published");
        }
        if (event.getParticipantLimit().equals(event.getRequest()) && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Event with id=%d has reached participant limit", eventId));
        }

        if (!event.getRequestModeration()) {
            event.setRequest(event.getRequest() + 1);

            eventRepository.save(EventMapper.toEventFromDto(event));
        }

        ParticipationRequestDto participationRequestDto;
        if (event.getParticipantLimit() == 0) {

            event.setRequestModeration(false);
            participationRequestDto = RequestMapper.toParticipationRequestDto(requestRepository.save(
                    RequestMapper.toRequest(EventMapper.toEventFromDto(event), UserMapper.toUser(user))));

        } else {
            participationRequestDto = RequestMapper.toParticipationRequestDto(requestRepository.save(
                    RequestMapper.toRequest(EventMapper.toEventFromDto(event), UserMapper.toUser(user))));
        }

        log.info("Request was successfully created by UserID= {} to Event by ID= {}", userId, eventId);
        return participationRequestDto;
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id="+ userId +
                        "and requesterId="+ requestId +" was not found"));

        request.setStatus(Status.CANCELED);
        ParticipationRequestDto participationRequestDto =
                RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        log.info("Request by ID= {} of User with ID= {} was successfully CANCELED", requestId, userId);
        return participationRequestDto;
    }
}