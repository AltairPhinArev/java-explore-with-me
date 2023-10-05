package ru.practicum.ewmmainservice.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.comment.CommentDto;
import ru.practicum.ewmmainservice.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping
    public List<CommentDto> getCommentsByPageParamsByText(@RequestParam String text,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Admin GET request to get all comments by text");
        return commentService.searchCommentsByText(text,from,size);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentsByPageParamsAndEventId(@PathVariable Long eventId,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Admin GET request to get all comments by event");
        return commentService.getCommentByEvent(eventId,from,size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @RequestParam(defaultValue = "true") Boolean permissible) {
        log.info("Admin PATCH request to comment");
        return commentService.updateCommentByAdmin(commentId, permissible);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentById(@PathVariable Long commentId) {
        log.info("Admin DELETE request to remove comments by commentId");
        commentService.deleteCommentByAdmin(commentId);
    }
}