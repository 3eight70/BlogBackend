package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Comment.CreateCommentDto;
import com.example.BlogBackend.Models.Comment.EditCommentDto;
import com.example.BlogBackend.Models.User.User;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ICommentService {
    ResponseEntity<?> addComment(UUID id, CreateCommentDto createCommentDto, User user);
    ResponseEntity<?> getAllNestedComments(UUID id);
    ResponseEntity<?> editComment(UUID id, EditCommentDto editCommentDto);
    ResponseEntity<?> deleteComment(UUID id);
}
