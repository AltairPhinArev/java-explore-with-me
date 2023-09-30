package ru.practicum.ewmmainservice.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.enums.SortByState;
import ru.practicum.ewmmainservice.dto.event.EventFullDto;
import ru.practicum.ewmmainservice.dto.event.EventShortDto;
import ru.practicum.ewmmainservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "events")
public class PublicEventController {

    EventService eventService;

    @Autowired
    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getAllPublic(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "EVENT_DATE") SortByState sortByState,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            HttpServletRequest request) {        
        log.info("PUBLIC GET request to get all events by params");
        return eventService.getAllPublic(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sortByState, from, size, request);
    }

    @GetMapping("{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("PUBLIC GET request to get events by ID= {}", eventId);
        return eventService.getPublicEvent(eventId, request);
    }
}
