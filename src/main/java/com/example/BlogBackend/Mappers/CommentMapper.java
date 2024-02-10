package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.Comment.Comment;
import com.example.BlogBackend.Models.Comment.CommentDto;
import com.example.BlogBackend.Models.Comment.CreateCommentDto;
import com.example.BlogBackend.Models.User.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentMapper {
    public static Comment createCommentDtoToComment(CreateCommentDto createCommentDto, User user) {
        return new Comment(
                UUID.randomUUID(),
                LocalDateTime.now(),
                createCommentDto.getContent(),
                null,
                null,
                user.getId(),
                user.getFullName(),
                0,
                createCommentDto.getParentId()
        );
    }

    public static CommentDto commentToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getCreateTime(),
                comment.getContent(),
                comment.getModifiedDate(),
                comment.getDeleteDate(),
                comment.getAuthorId(),
                comment.getAuthor(),
                comment.getSubComments()
        );
    }
}
