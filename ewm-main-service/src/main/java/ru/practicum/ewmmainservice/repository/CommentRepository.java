package ru.practicum.ewmmainservice.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.model.Comment;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(User user, PageRequest pageRequest);

    List<Comment> findAllByEventId(Event event, PageRequest pageRequest);

    List<Comment> findAllByEventId_Id(Long eventId);

    List<Comment> searchCommentByDescriptionContainsIgnoreCase(String text, PageRequest pageRequest);

    List<Comment> searchCommentsByUserId_IdAndDescriptionContainsIgnoreCase(Long userId, String text,
                                                                               PageRequest pageRequest);

    boolean existsByEventId_Id(Long eventId);

    @Query("SELECT c FROM Comment AS c " +
            "JOIN c.userId AS u " +
            "WHERE u.id = (:userId)" +
            "AND c.createdOn > :startDate " +
            "AND (cast(:endDate as date) IS NULL OR c.createdOn < :endDate)")
    List<Comment> findCommentByOwnerByStartDateAndEndDate(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                                          PageRequest pageRequest);

}
