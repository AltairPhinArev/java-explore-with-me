package ru.practicum.ewmstatsservice.mapper;

import org.springframework.stereotype.Component;
import dto.ViewStatsDto;

import ru.practicum.ewmstatsservice.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatsMapper {

    public static ViewStats toEntity(ViewStatsDto viewStatsDto) {
        return ViewStats.builder()
                .app(viewStatsDto.getApp())
                .uri(viewStatsDto.getUri())
                .hits(viewStatsDto.getHits())
                .build();
    }

    public static ViewStatsDto toDto(ViewStats stats) {
        return ViewStatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(stats.getHits())
                .build();
    }

    public static List<ViewStatsDto> toList(List<ViewStats> viewStats) {
        return viewStats.stream().map(StatsMapper::toDto).collect(Collectors.toList());
    }
}
