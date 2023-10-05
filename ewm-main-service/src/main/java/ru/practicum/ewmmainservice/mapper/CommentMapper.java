package ru.practicum.ewmmainservice.mapper;

import ru.practicum.ewmmainservice.dto.comment.CommentDto;
import ru.practicum.ewmmainservice.dto.comment.CommentDtoToCreateAndUpdate;
import ru.practicum.ewmmainservice.dto.comment.CommentShortDto;
import ru.practicum.ewmmainservice.dto.event.EventShortDto;
import ru.practicum.ewmmainservice.dto.user.UserShortDto;
import ru.practicum.ewmmainservice.model.Comment;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toCommentCreat(CommentDtoToCreateAndUpdate commentDtoToCreateAndUpdate,
                                            UserShortDto userDto,
                                            EventShortDto eventDto) {
        return CommentDto.builder()
                .id(null)
                .user(userDto)
                .event(eventDto)
                .description(commentDtoToCreateAndUpdate.getDescription())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User user, Event event) {
        return Comment.builder()
                .id(commentDto.getId())
                .userId(user)
                .eventId(event)
                .description(commentDto.getDescription())
                .createdOn(commentDto.getCreatedOn())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(UserMapper.toUserShortDto(comment.getUserId()))
                .event(EventMapper.toEventShortDto(comment.getEventId()))
                .description(comment.getDescription())
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .user(UserMapper.toUserShortDto(comment.getUserId()))
                .description(comment.getDescription())
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public static CommentShortDto toCommentShortDto(CommentDto comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .user(comment.getUser())
                .description(comment.getDescription())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}
