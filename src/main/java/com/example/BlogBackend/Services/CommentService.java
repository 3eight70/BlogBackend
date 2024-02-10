package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.CommentMapper;
import com.example.BlogBackend.Models.Comment.Comment;
import com.example.BlogBackend.Models.Comment.CreateCommentDto;
import com.example.BlogBackend.Models.Comment.EditCommentDto;
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
import java.util.List;
import java.util.UUID;

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
        Comment comment = CommentMapper.createCommentDtoToComment(createCommentDto, user);

        if (parentComment == null && createCommentDto.getParentId() != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Комментария-родителя не существует"), HttpStatus.NOT_FOUND);
        } else if (parentComment == null) {
            fullPost.getComments().add(comment);

            postRepository.save(fullPost);
        } else if (parentComment != null) {
            parentComment.setSubComments(parentComment.getSubComments() + 1);
            Comment parent = getRootParent(parentComment.getParentId(), parentComment.getId());
            if (parent != parentComment) {
                parent.setSubComments(parent.getSubComments() + 1);
            }
        }

        commentRepository.save(comment);
        fullPost.updateComments();

        return ResponseEntity.ok().build();
    }

    private Comment getRootParent(UUID parentId, UUID currentId) {
        Comment parent = commentRepository.findCommentById(parentId);
        if (parent != null) {
            return getRootParent(parent.getParentId(), parent.getId());
        }
        return commentRepository.findCommentById(currentId);
    }


    @Transactional
    public ResponseEntity<?> getAllNestedComments(UUID id) {
        Comment rootComment = commentRepository.findCommentById(id);
        if (rootComment == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного комментария не существует"), HttpStatus.NOT_FOUND);
        }

        List<Comment> allNestedComments = new ArrayList<>();
        getAllNestedCommentsRecursively(rootComment, allNestedComments);
        return ResponseEntity.ok(allNestedComments);
    }

    private void getAllNestedCommentsRecursively(Comment comment, List<Comment> allNestedComments) {
        List<Comment> subComments = commentRepository.findCommentsByParentId(comment.getId());
        allNestedComments.addAll(subComments);
        for (Comment subComment : subComments) {
            getAllNestedCommentsRecursively(subComment, allNestedComments);
        }
    }


    @Transactional
    public ResponseEntity<?> editComment(UUID id, EditCommentDto editCommentDto) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного комментария не существует"), HttpStatus.NOT_FOUND);
        } else if (comment.getDeleteDate() != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Комментарий удален"), HttpStatus.BAD_REQUEST);
        }

        comment.setContent(editCommentDto.getContent());
        comment.setModifiedDate(LocalDateTime.now());

        commentRepository.save(comment);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteComment(UUID id) {
        Comment comment = commentRepository.findCommentById(id);
        Comment parentComment;

        if (comment == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного комментария не существует"), HttpStatus.NOT_FOUND);
        } else if (comment.getDeleteDate() != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Комментарий уже удален"), HttpStatus.BAD_REQUEST);
        }
        UUID parentId = comment.getParentId();

        if (comment.getSubComments() == 0) {
            FullPost commentFullPost = postRepository.findByCommentsContains(comment);

            if (commentFullPost != null) {
                commentFullPost.getComments().remove(comment);
                commentFullPost.updateComments();
            }

            commentRepository.delete(comment);

        } else {
            comment.setDeleteDate(LocalDateTime.now());
            comment.setContent(" ");
        }

        if (parentId != null && comment.getSubComments() == 0) {
            parentComment = commentRepository.findCommentById(parentId);
            Comment parent = getRootParent(parentComment.getParentId(), parentComment.getId());
            parentComment.setSubComments(parentComment.getSubComments() - 1);

            if (parentComment != parent) {
                parent.setSubComments(parent.getSubComments() - 1);
            }

            FullPost fullPost = postRepository.findByCommentsContains(parentComment);

            if (parentComment.getDeleteDate() != null) {
                if (fullPost != null) {
                    fullPost.getComments().remove(parentComment);
                    fullPost.updateComments();
                }

                if (parentComment.getSubComments() == 0) {
                    if (parentComment != parent) {
                        parent.setSubComments(parent.getSubComments() - 1);
                    }

                    commentRepository.delete(parentComment);
                }
            }
        }

        return ResponseEntity.ok().build();
    }
}
