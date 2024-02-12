package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Comment.CreateCommentDto;
import com.example.BlogBackend.Models.Comment.EditCommentDto;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api")
public class CommentController {
    private final ICommentService commentService;

    @GetMapping("/comment/{id}/tree")
    public ResponseEntity<?> getNestedComments(@PathVariable UUID id) {
        try {
            return commentService.getAllNestedComments(id);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/{id}/comment")
    public ResponseEntity<?> addCommentToPost(@PathVariable UUID id, @RequestBody CreateCommentDto createCommentDto, @AuthenticationPrincipal User user) {
        try {
            return commentService.addComment(id, createCommentDto, user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<?> editComment(@PathVariable UUID id, @RequestBody EditCommentDto editCommentDto) {
        try {
            return commentService.editComment(id, editCommentDto);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
        try {
            return commentService.deleteComment(id);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
