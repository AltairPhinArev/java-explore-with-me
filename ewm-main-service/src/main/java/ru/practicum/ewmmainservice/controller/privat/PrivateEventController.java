package ru.practicum.ewmmainservice.controller.privat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.comment.CommentDto;
import ru.practicum.ewmmainservice.dto.comment.CommentDtoToCreateAndUpdate;
import ru.practicum.ewmmainservice.dto.event.*;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.service.CommentService;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
public class PrivateEventController {


    EventService eventService;

    CommentService commentService;

    RequestService requestService;

    @Lazy
    @Autowired
    public PrivateEventController(EventService eventService, CommentService commentService,
                                  RequestService requestService) {
        this.eventService = eventService;
        this.commentService = commentService;
        this.requestService = requestService;
    }

    @GetMapping("/events")
    public Set<EventShortDto> getAll(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
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

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createNewComment(@PathVariable Long userId, @PathVariable Long eventId,
                                       @RequestBody @Valid CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate) {
        return commentService.creteComment(userId, eventId, commentDtoToCreateAndUpdate);
    }

    @GetMapping("/events/comments")
    public List<CommentDto> getCommentByText(@PathVariable Long userId,
                                             @RequestParam(required = false) String text,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.searchUserCommentsByText(userId,text, from, size);
    }

    @GetMapping("/comments")
    public List<CommentDto> getCommentByParams(@PathVariable Long userId,
                                             @RequestParam(required = false) LocalDateTime startTime,
                                             @RequestParam(required = false) LocalDateTime endTime,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getOwnCommentsByParams(userId, startTime, endTime, from, size);
    }

    @PatchMapping("/events/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                    @RequestBody @Valid CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate) {
        return commentService.updateCommentByUser(userId, commentId, commentDtoToCreateAndUpdate);
    }

    @GetMapping("/events/comments/{commentId}")
    public CommentDto getCommentByIdFromUser(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.getOwnCommentById(userId, commentId);
    }

    @GetMapping("/events/comments/")
    public List<CommentDto> getCommentByIdFromUser(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getOwnComments(userId, from, size);
    }


    @DeleteMapping("/events/comments/{commentId}")
    public void deleteOwnComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
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