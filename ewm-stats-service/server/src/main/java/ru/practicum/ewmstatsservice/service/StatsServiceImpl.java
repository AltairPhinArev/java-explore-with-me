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
import java.util.*;
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
            log.info("Unique GET request of stats with Parameters start-{}, end-{}, uris-{}", start, end, uris);
            return statsRepository.findUniqueViewStats(startTime, endTime, uris)
                    .stream()
                    .map(StatsMapper::toDto)
                    .sorted(Comparator.comparingLong(ViewStatsDto::getHits))
                    .collect(Collectors.toList());
        }

        if (uris == null) {
            log.info("GET stats request without List of Uris");
            uris = statsRepository.findAllByTimestampBetween(startTime, endTime)
                    .stream()
                    .map(EndpointHit::getUri)
                    .distinct()
                    .collect(Collectors.toList());
        }

        for (String uri : uris) {
            List<EndpointHit> hits = statsRepository.findAllByUriAndTimestampBetween(uri, startTime, endTime);
            if (!hits.isEmpty()) {
                list.add(StatsMapper.toDto(Objects.requireNonNull(HitMapper.toViewStats(hits))));
            }
        }
        list.sort(Comparator.comparingLong(ViewStatsDto::getHits).reversed());
        return list;
    }
    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        log.info("EndpointHitDto was created by parameters {} | {} | {}", endpointHitDto.getApp()
                ,endpointHitDto.getUri(),endpointHitDto.getIp());
        return HitMapper.toDto(statsRepository.save(HitMapper.toEntity(endpointHitDto)));
    }
}
