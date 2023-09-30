package ru.practicum.ewmmainservice.service;


import ru.practicum.ewmmainservice.dto.SortByState;

import ru.practicum.ewmmainservice.dto.State;
import ru.practicum.ewmmainservice.dto.event.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventDto> getByCategoryId(Long catId);

    Set<EventShortDto> getAll(Long userId, Integer from, Integer size);

    EventFullDto get(Long userId, Long eventId);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto UserUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);

    EventDto getEventById(Long eventId);

    List<EventFullDto> getAllToAdmin(List<Long> users, List<State> states,
                              List<Long> categories, String rangeStart,
                                     String rangeEnd, Integer from, Integer size);

    EventFullDto getPublicEvent(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllPublic(String text, List<Long> categories, Boolean paid, String  rangeStart,
                                     String  rangeEnd, Boolean onlyAvailable, SortByState sortByState, Integer from,
                                     Integer size, HttpServletRequest request);
}
