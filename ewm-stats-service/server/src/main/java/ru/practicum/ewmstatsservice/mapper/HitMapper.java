package ru.practicum.ewmstatsservice.mapper;

import org.springframework.stereotype.Component;
import dto.EndpointHitDto;
import ru.practicum.ewmstatsservice.model.EndpointHit;
import ru.practicum.ewmstatsservice.model.ViewStats;

import java.util.List;

@Component
public class HitMapper {

    public static EndpointHit toEntity(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .id(endpointHitDto.getId())
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

    public static ViewStats toViewStats(List<EndpointHit> list) {
        if (list.isEmpty()) {
            return null;
        }
        return ViewStats.builder()
                .app(list.get(0).getApp())
                .uri(list.get(0).getUri())
                .hits(Long.valueOf(list.size()))
                .build();
    }
}
