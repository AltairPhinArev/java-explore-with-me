package ru.practicum.ewmstatsservice.service;

import dto.ViewStatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.ewmstatsservice.mapper.StatsMapper;
import ru.practicum.ewmstatsservice.model.EndpointHit;
import ru.practicum.ewmstatsservice.mapper.HitMapper;

import dto.EndpointHitDto;
import ru.practicum.ewmstatsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static contstants.ConstantUtil.DATE_TIME_FORMAT;


@Service
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {

    StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public Collection<ViewStatsDto> get(String start, String end, List<String> uris, Boolean unique) {

        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

        List<ViewStatsDto> list = new ArrayList<>();

        if (unique) {
            return statsRepository.findUniqueViewStats(startTime, endTime, uris).stream()
                    .map(StatsMapper::toDto)
                    .collect(Collectors.toList());

        } else {
            if (uris == null) {
                uris = statsRepository.findAllByTime(startTime, endTime).stream()
                        .distinct()
                        .collect(Collectors.toList());
            }
            List<EndpointHit> hits;
            for (String uri : uris) {
                if (uri.contains("[")) {
                    hits = statsRepository.findAllByUri(uri.substring(1, uri.length() - 1), startTime, endTime);
                } else {
                    hits = statsRepository.findAllByUri(uri, startTime, endTime);
                }
                if (!hits.isEmpty()) {
                    list.add(StatsMapper.toDto(HitMapper.toViewStats(hits)));
                }
            }
        }
        list.sort(Comparator.comparingLong(ViewStatsDto::getHits).reversed());
        return list;
    }

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto dto) {
        return HitMapper.toDto(statsRepository.save(HitMapper.toEntity(dto)));
    }
}
