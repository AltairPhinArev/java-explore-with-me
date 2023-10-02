package ru.practicum.ewmmainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmainservice.model.Request;
import ru.practicum.ewmmainservice.model.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByRequester(User requester);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);
}
