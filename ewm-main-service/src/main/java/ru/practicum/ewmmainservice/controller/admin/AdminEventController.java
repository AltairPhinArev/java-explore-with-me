package ru.practicum.ewmmainservice.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.State;
import ru.practicum.ewmmainservice.dto.event.*;

import ru.practicum.ewmmainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String startTime,
                                        @RequestParam(required = false) String endTime,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Admin GET request to get all events by params users-{}, states-{}, categories-{}" +
                ",startTime-{}, endTime-{}", users,states,categories, startTime,endTime);
        return eventService.getAllToAdmin(users, states, categories, startTime, endTime, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest updateEvent) {
        log.info("Admin PATCH request to update event by ID= {}", eventId);
        return eventService.adminUpdateEvent(eventId, updateEvent);
    }
}
