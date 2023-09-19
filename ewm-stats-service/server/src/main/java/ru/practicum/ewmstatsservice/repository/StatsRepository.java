package ru.practicum.ewmstatsservice.repository;

import ru.practicum.ewmstatsservice.model.EndpointHit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmstatsservice.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select h.uri from EndpointHit h where h.timestamp>=?1 AND h.timestamp<=?2")
    List<String> findAllByTime(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select h from EndpointHit h where h.uri=?1 AND h.timestamp>=?2 AND h.timestamp<=?3")
    List<EndpointHit> findAllByUri(String uri, LocalDateTime startTime, LocalDateTime endTime);

    @Query(" SELECT new ru.practicum.ewmstatsservice.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "AND (eh.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC ")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}

