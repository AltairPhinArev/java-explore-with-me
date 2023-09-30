package ru.practicum.ewmmainservice.controller.privat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.event.*;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.RequestService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
public class PrivateEventController {


    EventService eventService;

    RequestService requestService;

    @Lazy
    @Autowired
    public PrivateEventController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @GetMapping("/events")
    public Set<EventShortDto> getAll(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") Integer from ,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Private GET request from User = {} to get All events", userId);
        return eventService.getAll(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Private GET request from User = {} to get event by ID= {}", userId, eventId);
        return eventService.get(userId, eventId);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto eventDto) {
        log.info("Private POST request from User = {} to Post event", userId);
        return eventService.create(userId, eventDto);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("Private PATCH request from User = {} to update event by ID= {}", userId, eventId);
        return eventService.userUpdateEvent(userId, eventId, eventDto);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody  EventRequestStatusUpdateRequest request) {
        log.info("Private PATCH request from User = {} to update status of event by ID= {}", userId, eventId);
        return eventService.updateRequestStatus(userId, eventId, request);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Private GET request from User = {} to get Requests by event with ID= {}", userId, eventId);
        return requestService.getRequestsByEvent(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getAlRequests(@PathVariable Long userId) {
        log.info("Private GET request from User = {} to get All Requests", userId);
        return requestService.getRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Private POST request from User = {} to create Request in Event by ID= {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestsId}/cancel")
    public ParticipationRequestDto update(@PathVariable Long userId, @PathVariable Long requestsId) {
        log.info("Private PATCH request from User = {} to update Request by ID= {}", userId, requestsId);
        return requestService.updateRequest(userId, requestsId);
    }

}