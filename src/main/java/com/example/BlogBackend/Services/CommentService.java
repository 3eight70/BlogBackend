package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.CommentMapper;
import com.example.BlogBackend.Models.Comment.Comment;
import com.example.BlogBackend.Models.Comment.CommentDto;
import com.example.BlogBackend.Models.Comment.CreateCommentDto;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Repositories.CommentRepository;
import com.example.BlogBackend.Repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ResponseEntity<?> addComment(UUID id, CreateCommentDto createCommentDto, User user) {
        FullPost fullPost = postRepository.findPostFullDtoById(id);
        if (fullPost == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }

        Comment parentComment = commentRepository.findCommentById(createCommentDto.getParentId());

        if (parentComment == null && createCommentDto.getParentId() != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Комментария-родителя не существует"), HttpStatus.NOT_FOUND);
        } else if (parentComment != null) {
            parentComment.setSubComments(parentComment.getSubComments() + 1);
        }

        Comment comment = CommentMapper.createCommentDtoToComment(createCommentDto, user, fullPost.getId());

        fullPost.getComments().add(comment);

        postRepository.save(fullPost);
        commentRepository.save(comment);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getAllNestedComments(UUID id) {
        FullPost fullPost = postRepository.findPostFullDtoById(id);
        if (fullPost == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }

        List<CommentDto> commentDtoList = new ArrayList<>();

        fullPost.getComments().stream()
                .sorted(this::compareTime)
                .map(CommentMapper::commentToCommentDto)
                .forEach(commentDtoList::add);

        return ResponseEntity.ok(commentDtoList);
    }

    @Transactional
    public ResponseEntity<?> editComment(UUID id, String content) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного комментария не существует"), HttpStatus.NOT_FOUND);
        }
        else if (comment.getDeleteDate() != null){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Комментарий удален"), HttpStatus.BAD_REQUEST);
        }

        comment.setContent(content);
        comment.setModifiedDate(LocalDateTime.now());

        commentRepository.save(comment);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteComment(UUID id) {
        Comment comment = commentRepository.findCommentById(id);
        Comment parentComment;
        FullPost fullPost;

        if (comment == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного комментария не существует"), HttpStatus.NOT_FOUND);
        }
        else if (comment.getDeleteDate() != null){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Комментарий уже удален"), HttpStatus.BAD_REQUEST);
        }
        else {
            fullPost = postRepository.findPostFullDtoById(comment.getPostId());
        }
        UUID parentId = comment.getParentId();

        if (comment.getSubComments() == 0) {
            deleteComment(fullPost, comment);
        } else {
            comment.setDeleteDate(LocalDateTime.now());
            comment.setContent(" ");
        }

        if (parentId != null) {
            parentComment = commentRepository.findCommentById(parentId);
            parentComment.setSubComments(parentComment.getSubComments() - 1);

            if (parentComment.getSubComments() == 0){
                deleteComment(fullPost, parentComment);
            }
        }

        return ResponseEntity.ok().build();
    }

    private void deleteComment(FullPost fullPost, Comment comment){
        fullPost.getComments().remove(comment);

        postRepository.save(fullPost);
        commentRepository.delete(comment);
    }
    private int compareTime(Comment firstComment, Comment secondComment) {
        return firstComment.getCreateTime().compareTo(secondComment.getCreateTime());
    }
}
