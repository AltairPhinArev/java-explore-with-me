package ru.practicum.ewmmainservice.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.dto.State;
import ru.practicum.ewmmainservice.model.Category;
import ru.practicum.ewmmainservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmainservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(Category category);

    Set<Event> findAllByIdIn(Set<Long> ids);

    Optional<Event> findByIdAndInitiatorId(Long id, User userId);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "JOIN e.initiatorId AS u " +
            "WHERE ((:users) IS NULL OR u.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR c.id IN :categories) " +
            "AND e.eventDate > :rangeStart " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd)")
    List<Event> findEventsByParams(List<Long> users, List<State> states, List<Long> categories,
                          LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageable);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "WHERE ((:text) IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.title) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :rangeStart " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd) " +
            "AND e.state = 'PUBLISHED' " +
            "ORDER BY e.views DESC")
    List<Event> findPublicSortByViews(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, PageRequest pageRequest);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "WHERE ((:text) IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.title) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :startDate " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd) " +
            "AND e.state = 'PUBLISHED' " +
            "ORDER BY e.eventDate")
    List<Event> findPublicSortByEventDate(String text, List<Long> categories, Boolean paid, LocalDateTime startDate,
                                          LocalDateTime rangeEnd, PageRequest pageRequest);

    boolean existsByIdAndInitiatorId(Long id, User userId);
}