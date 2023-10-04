package ru.practicum.ewmmainservice.service.impl;

import dto.EndpointHitDto;
import gateway.Client.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewmmainservice.dto.enums.*;
import ru.practicum.ewmmainservice.dto.event.*;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;

import ru.practicum.ewmmainservice.errorhandling.exceptions.ValidationException;
import ru.practicum.ewmmainservice.mapper.*;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.Request;
import ru.practicum.ewmmainservice.repository.EventRepository;
import ru.practicum.ewmmainservice.repository.RequestRepository;
import ru.practicum.ewmmainservice.service.CategoryService;
import ru.practicum.ewmmainservice.service.CommentService;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewmmainservice.dto.enums.Status.*;


@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    CategoryService categoryService;

    RequestRepository requestRepository;

    CommentService commentService;

    UserService userService;

    StatsClient statsClient;

    @Lazy
    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryService categoryService,
                            RequestRepository requestRepository, CommentService commentService,
                            UserService userService, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.categoryService = categoryService;
        this.requestRepository = requestRepository;
        this.commentService = commentService;
        this.userService = userService;
        this.statsClient = statsClient;
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        EventFullDto eventFullDto;
        checkEventDate(newEventDto.getEventDate());
        Event event = EventMapper.toEventCreate(newEventDto);

        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }

        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(newEventDto.getCategory())));
        event.setPublishDate(LocalDateTime.now());
        event.setInitiatorId(UserMapper.toUser(userService.getUserById(userId)));
        event.setViews(0L);
        event.setRequest(0L);


        try {
             eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
             log.info("Event was successfully created");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }

        return eventFullDto;
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        if (updateEventAdminRequest.getEventDate() != null) {
            checkEventDate(updateEventAdminRequest.getEventDate());
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " was not found"));


        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (event.getState() != null) {

            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED");
            } else if (event.getState().equals(State.CANCELED)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: CANCELED");
            } else {
                if (updateEventAdminRequest.getStateAction() != null) {

                    if (updateEventAdminRequest.getStateAction().toString()
                            .equals(AdminStateAction.PUBLISH_EVENT.toString())) {
                        event.setState(State.PUBLISHED);
                    }
                    if (updateEventAdminRequest.getStateAction().toString()
                            .equals(AdminStateAction.REJECT_EVENT.toString())) {
                        event.setState(State.CANCELED);
                    }
                }
            }
        }
        try {
            log.info("Event was updated By Admin");
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
    }

    @Override
    public List<EventFullDto> getAllToAdmin(List<Long> users, List<State> states,
                                            List<Long> categories, String dateStart, String dateEnd,
                                            Integer from, Integer size) {

        LocalDateTime startDate = getStartDate(dateStart);
        LocalDateTime endDate = getEndDate(dateEnd);

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException("End of range can't be before start");
        }

        return eventRepository.findEventsByParams(users, states, categories, startDate,
                        endDate, PageRequest.of(from, size)).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(String text, List<Long> categories,
                                            Boolean paid, String  rangeStart,
                                            String  rangeEnd, Boolean onlyAvailable,
                                            SortByState sortByState, Integer from, Integer size,
                                            HttpServletRequest request) {
        List<Event> events = new ArrayList<>();

        LocalDateTime startDate = getStartDate(rangeStart);
        LocalDateTime endDate = getEndDate(rangeEnd);

        checkEventDate(endDate);

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException("End of range cant be before start");
        }

        if (sortByState != null && sortByState.equals(SortByState.EVENT_DATE)) {
            events = eventRepository.findPublicSortByEventDate(text, categories, paid, startDate,
                    endDate, PageRequest.of(from, size));
        } else if (sortByState != null && sortByState.equals(SortByState.VIEWS)) {
            events = eventRepository.findPublicSortByViews(text, categories, paid, startDate,
                    endDate, PageRequest.of(from, size));
        }

        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getParticipants().size() < event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        int defaultSize = eventShortDtoList.size();
        for (int i = 0; i < size - defaultSize; i++) {
            eventShortDtoList.add(null);
        }

        saveEndpointHit(request);
        return eventShortDtoList;
    }

    @Override
    public List<EventDto> getByCategoryId(Long catId) {
        if (categoryService.checkCategory(catId) &&
                eventRepository.findByCategory(
                        CategoryMapper.toCategory(categoryService.getCategoryById(catId))) != null) {

            return eventRepository.findByCategory(
                    CategoryMapper.toCategory(categoryService.getCategoryById(catId))).stream()
                    .map(EventMapper::toEventDto)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        Set<String> uniqView = new HashSet<>();
        uniqView.add(request.getRemoteAddr());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + "not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not published");
        }

        saveEndpointHit(request);
        event.setViews((long) uniqView.size());
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        if (commentService.checkCommentByEventId(eventId)) {
            eventFullDto.setComments(commentService.getCommentToEvent(eventId).stream()
                    .map(CommentMapper::toCommentShortDto)
                    .collect(Collectors.toList()));
        }

        log.info("Public get Event by Id= {}", eventId);
        return eventFullDto;
    }

    @Override
    public Set<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        userService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        return eventRepository.findAll(pageRequest).toSet().stream()
                .map(EventMapper::toEventShortDto)
                .sorted(Comparator.comparing(EventShortDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId,
                        UserMapper.toUser(userService.getUserById(userId)))
                .orElseThrow(() -> new NotFoundException(
                       "Event not found with id = " + eventId + " and userId =" + userId)));

        if (commentService.checkCommentByEventId(eventId)) {
            eventFullDto.setComments(commentService.getCommentToEvent(eventId).stream()
                    .map(CommentMapper::toCommentShortDto)
                    .collect(Collectors.toList()));
        }

        return eventFullDto;
    }

    @Override
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, UserMapper.toUser(
                userService.getUserById(userId)
                ))
                .orElseThrow(() -> new NotFoundException(
                       "Event not found with id =" + eventId + " and userId =" + userId));
        Event eventUpdate = EventMapper.toEventUpdateByUser(updateEventUserRequest);

        if (updateEventUserRequest.getCategory() != null) {
            eventUpdate.setCategory(
                    CategoryMapper.toCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory())));
        }
            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Event must not be published");
            }
        if (updateEventUserRequest.getStateAction() != null) {
            if (UserStateAction.CANCEL_REVIEW.toString().equals(updateEventUserRequest.getStateAction().toString())) {
                event.setState(State.CANCELED);

            } else if (UserStateAction.SEND_TO_REVIEW.toString().equals(updateEventUserRequest.getStateAction().toString())) {
                event.setState(State.PENDING);
            }
        }

        checkEventDate(eventUpdate.getEventDate());
        eventRepository.save(event);

        log.info("Event was updated by ID/State -> {}/{}", event.getId(), event.getState());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long
            eventId, EventRequestStatusUpdateRequest updateRequest) {

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        List<Long> requestIds = new ArrayList<>(new HashSet<>(updateRequest.getRequestIds()));
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestIds);

        for (Request request : requestsToUpdate) {
            if (event.getParticipants().size() >= event.getParticipantLimit()) {
                throw new ConflictException("Event participant limit reached");
            }

            if (!request.getStatus().equals(PENDING)) {
                throw new ConflictException("Request status must be PENDING");
            }

            request.setStatus(Status.valueOf(updateRequest.getStatus()));
        }

        requestRepository.saveAll(requestsToUpdate);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        List<ParticipationRequestDto> participationRequestsConfirmed = requests.stream()
                .filter(request -> CONFIRMED.equals(request.getStatus()))
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> participationRequestsRejected = requests.stream()
                .filter(request -> REJECTED.equals(request.getStatus()))
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        eventRequestStatusUpdateResult.setConfirmedRequests(participationRequestsConfirmed);
        eventRequestStatusUpdateResult.setRejectedRequests(participationRequestsRejected);

        event.setRequest(event.getRequest() + (long) eventRequestStatusUpdateResult.getConfirmedRequests().size());

        if (!eventRequestStatusUpdateResult.getRejectedRequests().isEmpty())  {
            event.setRequest(event.getRequest() - (long) eventRequestStatusUpdateResult.getRejectedRequests().size());
            if (event.getRequest() < 0) {
                event.setRequest(0L);
            }
        }

        eventRepository.save(event);
        log.info("Event was updated by ID -> {}", event.getId());
        return eventRequestStatusUpdateResult;
    }

    @Override
    public EventDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id =" + eventId + " not found"));
        return EventMapper.toEventDto(event);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Field: eventDate. Error: the date and time for which the event is scheduled" +
                        " cannot be earlier than two hours from the current moment. Value: " + eventDate);
            }
        }
    }

    private void saveEndpointHit(HttpServletRequest request) {
        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.save(endpointHit);
    }

    private LocalDateTime getStartDate(String rangeStart) {
        LocalDateTime startDate;
        if (rangeStart == null || rangeStart.isBlank()) {
            startDate = LocalDateTime.now();
        } else {
            startDate = LocalDateTime.parse(
                    URLDecoder.decode(rangeStart, StandardCharsets.UTF_8),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
        }
        return startDate;
    }

    private LocalDateTime getEndDate(String rangeEnd) {
        LocalDateTime endDate;
        if (rangeEnd == null || rangeEnd.isBlank()) {
            endDate = null;
        } else {
            endDate = LocalDateTime.parse(
                    URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
        }
        return endDate;
    }
}