package ru.practicum.ewmstatsservice.repository;

import ru.practicum.ewmstatsservice.model.EndpointHit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmstatsservice.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    //@Query("SELECT hit.uri FROM EndpointHit hit WHERE hit.timestamp>=?1 AND hit.timestamp<=?2")
    List<EndpointHit> findAllByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<EndpointHit> findAllByUriAndTimestampBetween(String uri, LocalDateTime startTime, LocalDateTime endTime);

    @Query( "SELECT new ru.practicum.ewmstatsservice.model.ViewStats(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN ?1 AND ?2 " +
            "AND (hit.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(DISTINCT hit.ip) DESC ")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}

