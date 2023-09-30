package ru.practicum.ewmmainservice.service.impl;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import gateway.Client.StatsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmainservice.dto.*;

import ru.practicum.ewmmainservice.dto.event.*;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;

import ru.practicum.ewmmainservice.errorhandling.exceptions.ValidationException;
import ru.practicum.ewmmainservice.mapper.CategoryMapper;
import ru.practicum.ewmmainservice.mapper.EventMapper;
import ru.practicum.ewmmainservice.mapper.RequestMapper;
import ru.practicum.ewmmainservice.mapper.UserMapper;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.Request;
import ru.practicum.ewmmainservice.repository.EventRepository;
import ru.practicum.ewmmainservice.repository.RequestRepository;
import ru.practicum.ewmmainservice.service.CategoryService;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewmmainservice.dto.Status.CONFIRMED;
import static ru.practicum.ewmmainservice.dto.Status.REJECTED;


@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    CategoryService categoryService;

    RequestRepository requestRepository;

    UserService userService;

    StatsClient statsClient;

    @Lazy
    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryService categoryService,
                            RequestRepository requestRepository, UserService userService,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.categoryService = categoryService;
        this.requestRepository = requestRepository;
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

        return eventRepository.findEventsByParams(users, states, categories,
                        startDate, endDate, PageRequest.of(from , size)).stream()
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

        if (sortByState != null && sortByState.toString().equals("EVENT_DATE")) {
            events = eventRepository.findPublicSortByEventDate(text, categories, paid, startDate,
                    endDate, PageRequest.of(from, size));
        } else if (sortByState != null && sortByState.toString().equals("VIEWS")) {
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
                .orElseThrow(() -> new NotFoundException("Event with id ="+ eventId+ "not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId +" is not published");
        }

        saveEndpointHit(request);
        event.setViews((long) uniqView.size());
        log.info("Public get Event by Id= {}", eventId);
        return EventMapper.toEventFullDto(event);
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
        return EventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId,
                        UserMapper.toUser(userService.getUserById(userId)))
                .orElseThrow(() -> new NotFoundException(
                       "Event not found with id = " + eventId + " and userId =" + userId)));
    }

    @Override
    public EventFullDto UserUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) {

        Event eventTarget = eventRepository.findByIdAndInitiatorId(eventId, UserMapper.toUser(
                userService.getUserById(userId)
                ))
                .orElseThrow(() -> new NotFoundException(
                       "Event not found with id =" + eventId + " and userId =" + userId));
        Event eventUpdate = EventMapper.toEventUpdateByUser(eventDto);

        if (eventDto.getCategory() != null) {
            eventUpdate.setCategory(
                    CategoryMapper.toCategory(categoryService.getCategoryById(eventDto.getCategory())));
        }
            if (eventTarget.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Event must not be published");
            }
        if (eventDto.getStateAction() != null) {
            if (UserStateAction.CANCEL_REVIEW.toString().equals(eventDto.getStateAction().toString())) {
                eventTarget.setState(State.CANCELED);

            } else if (UserStateAction.SEND_TO_REVIEW.toString().equals(eventDto.getStateAction().toString())) {
                eventTarget.setState(State.PENDING);
            }
        }

        checkEventDate(eventUpdate.getEventDate());
        eventRepository.save(eventTarget);

        log.info("Update event: {}", eventTarget.getTitle());
        return EventMapper.toEventFullDto(eventTarget);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long
            eventId, EventRequestStatusUpdateRequest request) {

        List<ParticipationRequestDto> confirmedRequests = List.of();
        List<ParticipationRequestDto> rejectedRequests = List.of();

        List<Long> requestIds = request.getRequestIds();
        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        String status = request.getStatus();

        if (status.equals(REJECTED.toString())) {
            if (status.equals(REJECTED.toString())) {
                boolean isConfirmedRequestExists = requests.stream()
                        .anyMatch(r -> r.getStatus().equals(CONFIRMED));
                if (isConfirmedRequestExists) {
                    throw new ConflictException("Cannot reject confirmed requests");
                }
                rejectedRequests = requests.stream()
                        .peek(r -> r.setStatus(REJECTED))
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
            }
        }

        Event event = eventRepository.findByIdAndInitiatorId(
                eventId, UserMapper.toUser(userService.getUserById(userId)))
                .orElseThrow(() -> new NotFoundException(
                        "Event not found with id ="+ eventId +" and userId =" + userId));

        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getRequest();

        long availableParticipants = participantLimit - approvedRequests;
        long potentialParticipants = requestIds.size();

        if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
            throw new ConflictException("Event with id=" + eventId + " has reached participant limit");
        }

        if (status.equals(CONFIRMED.toString())) {
            if (participantLimit.equals(0L) || (potentialParticipants <= availableParticipants && !event.getRequestModeration())) {
                confirmedRequests = requests.stream()
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(
                                     "Request with id=" + r.getId() +" has already been confirmed");
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                event.setRequest(approvedRequests + potentialParticipants);
            } else {
                confirmedRequests = requests.stream()
                        .limit(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(
                                        "Request with id=" + r.getId() + " has already been confirmed");
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                rejectedRequests = requests.stream()
                        .skip(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(REJECTED)) {
                                r.setStatus(REJECTED);
                            } else {
                                throw new ConflictException(
                                        "Request with id=" + r.getId() + " has already been rejected");
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());

                event.setRequest((long) confirmedRequests.size());
            }
        }
        eventRepository.save(event);
        requestRepository.flush();
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
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