package ru.practicum.ewmmainservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.dto.comment.CommentDto;
import ru.practicum.ewmmainservice.dto.comment.CommentDtoToCreateAndUpdate;
import ru.practicum.ewmmainservice.dto.enums.State;
import ru.practicum.ewmmainservice.dto.event.EventDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ValidationException;
import ru.practicum.ewmmainservice.mapper.CommentMapper;
import ru.practicum.ewmmainservice.mapper.EventMapper;
import ru.practicum.ewmmainservice.mapper.UserMapper;
import ru.practicum.ewmmainservice.model.Comment;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.User;
import ru.practicum.ewmmainservice.repository.CommentRepository;
import ru.practicum.ewmmainservice.repository.EventRepository;
import ru.practicum.ewmmainservice.service.CommentService;
import ru.practicum.ewmmainservice.service.EventService;
import ru.practicum.ewmmainservice.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    EventService eventService;

    EventRepository eventRepository;

    CommentRepository commentRepository;

    UserService userService;

    @Lazy
    @Autowired
    public CommentServiceImpl(EventService eventService, EventRepository eventRepository,
                              CommentRepository commentRepository, UserService userService) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    @Override
    public CommentDto creteComment(Long userId, Long eventId, CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate) {
        CommentDto commentDto;
        EventDto eventDto = eventService.getEventById(eventId);

        if (userService.checkUser(userId) && eventRepository.existsById(eventId)) {
            if (eventService.getEventById(eventId).getState().equals(State.PUBLISHED)) {
                commentDto = CommentMapper.toCommentCreat(commentDtoToCreateAndUpdate,
                        UserMapper.toUserShortDto(userService.getUserById(userId)),
                        EventMapper.toEventShortDto(eventDto)
                );
                return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto,
                        UserMapper.toUser(userService.getUserById(userId)),
                        EventMapper.toEventFromDto(eventDto))));
            } else {
                throw new ConflictException("You can comment only PUBLISHED events");
            }
        } else {
         throw new NotFoundException("User or Event with ids" + userId.toString() + eventId.toString() + " were " +
                 "not found");
        }
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, Boolean permissible) {
        if (commentRepository.existsById(commentId)) {
            if (permissible) {
                return getCommentByIdForAdmin(commentId);
            } else {

                CommentDto commentDto = getCommentByIdForAdmin(commentId);
                commentDto.setDescription("This comment is not permissible, it's may be offensive");
                User user = UserMapper.toUser(userService.getUserById(commentDto.getUser().getId()));
                Event event = EventMapper.toEventFromDto(eventService.getEventById(commentDto.getEvent().getId()));

                commentRepository.save(CommentMapper.toComment(commentDto, user, event));
                return commentDto;

            }
        } else {
            throw new NotFoundException("Comment with id=" + commentId + "was not found");
        }
    }

    @Override
    public CommentDto updateCommentByUser(Long userId, Long commentId,
                                    CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate) {

        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + "was not found")));

        if (Objects.equals(commentDto.getUser().getId(), userId)) {
            commentDto.setDescription(commentDtoToCreateAndUpdate.getDescription());

            commentRepository.save(CommentMapper.toComment(commentDto,
                    UserMapper.toUser(userService.getUserById(userId)),
                    EventMapper.toEventFromDto(eventService.getEventById(commentDto.getEvent().getId()))));
            return commentDto;
        } else {
            throw new ConflictException("Only owner of comment can update comment");
        }
    }

    @Override
    public List<CommentDto> getOwnComments(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by("id"));
        if (userService.checkUser(userId)) {
            return commentRepository.findAllByUserId(UserMapper.toUser(userService.getUserById(userId)), pageRequest)
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public List<CommentDto> getOwnCommentsByParams(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                                   Integer from, Integer size) {
        userService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);


        if (startDate != null || endDate != null) {
            if (endDate.isAfter(startDate)) {
                throw new ValidationException("StartDate must be earlier EndDate");
            }

            return commentRepository.findCommentByOwnerByStartDateAndEndDate(userId, startDate, endDate, pageRequest)
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        } else {
            return commentRepository.findAll(pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto getOwnCommentById(Long userId, Long commentId) {
        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + "was not found")));

        if (Objects.equals(commentDto.getUser().getId(), userId)) {
            return commentDto;
        } else {
            throw new ConflictException("Only owner of comment can get comment");
        }
    }

    @Override
    public CommentDto getCommentByIdForAdmin(Long commentId) {
        return CommentMapper.toCommentDto(commentRepository.findById(commentId).
                orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found")));
    }

    @Override
    public List<CommentDto> searchCommentsByText(String text, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        if (text == null || text.isBlank()) {
            return commentRepository.findAll(pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        } else {
            return commentRepository.searchCommentByDescriptionContainsIgnoreCase(text, pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<CommentDto> searchUserCommentsByText(Long userId, String text, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        userService.checkUser(userId);

        if (text == null || text.isBlank()) {
            return commentRepository.findAllByUserId(UserMapper.toUser
                    (userService.getUserById(userId)), pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        } else {
            return commentRepository.searchCommentsByUserId_IdAndDescriptionContainsIgnoreCase(userId,
                            text, pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }
    }


    @Override
    public List<CommentDto> getCommentByEvent(Long eventId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        if (eventRepository.existsById(eventId)) {
            return commentRepository.findAllByEventId(EventMapper.toEventFromDto(eventService.getEventById(eventId)),
                            pageRequest).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Event By id=" + eventId + " was not found");
        }
    }

    @Override
    public List<CommentDto> getCommentToEvent(Long eventId) {
        return commentRepository.findAllByEventId_Id(eventId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkCommentByEventId(Long eventId) {
        return commentRepository.existsByEventId_Id(eventId);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + "was not found"));
        if (Objects.equals(comment.getUserId().getId(), userId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ConflictException("This user don't written this comm,ent");
        }
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException("Comment with id=" + commentId + "was not found");
        }
    }
}