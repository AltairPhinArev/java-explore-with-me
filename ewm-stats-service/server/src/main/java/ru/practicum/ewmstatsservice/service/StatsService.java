package ru.practicum.ewmstatsservice.service;

import dto.EndpointHitDto;
import dto.ViewStatsDto;

import java.util.Collection;
import java.util.List;

public interface StatsService {

    Collection<ViewStatsDto> get(String start, String end, List<String> uris, Boolean unique);

    EndpointHitDto create(EndpointHitDto dto);

}
