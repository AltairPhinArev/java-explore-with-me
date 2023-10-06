package ru.practicum.ewmmainservice.service;

import ru.practicum.ewmmainservice.dto.comment.CommentDto;
import ru.practicum.ewmmainservice.dto.comment.CommentDtoToCreateAndUpdate;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto creteComment(Long userId, Long eventId, CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentDto updateCommentByUser(Long userId, Long commentId, CommentDtoToCreateAndUpdate comment);

    CommentDto updateCommentByAdmin(Long commentId, Boolean permissible);

    List<CommentDto> getOwnComments(Long userId, Integer from, Integer size);

    List<CommentDto> getOwnCommentsByParams(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer from,
                                            Integer size);

    CommentDto getOwnCommentById(Long userId, Long commentId);

    CommentDto getCommentByIdForAdmin(Long commentId);

    List<CommentDto> searchCommentsByText(String text, Integer from, Integer size);

    List<CommentDto> searchUserCommentsByText(Long userId, String text, Integer from, Integer size);

    List<CommentDto> getCommentByEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentToEvent(Long eventId);

    boolean checkCommentByEventId(Long eventId);
}