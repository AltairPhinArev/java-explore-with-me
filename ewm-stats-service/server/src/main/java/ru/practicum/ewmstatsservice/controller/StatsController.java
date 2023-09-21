package ru.practicum.ewmstatsservice.controller;

import dto.ViewStatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dto.EndpointHitDto;

import ru.practicum.ewmstatsservice.service.StatsService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class StatsController {

    StatsService service;

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<Collection<ViewStatsDto>> get(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("GET REQUEST BY -> /stats");
        return new ResponseEntity<>(service.get(start, end, uris, unique), HttpStatus.OK);
    }

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> create(@RequestBody EndpointHitDto dto) {
        log.info("POST REQUEST BY -> /hit");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }
}
