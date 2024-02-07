package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Comment findCommentById(UUID id);

    List<Comment> findCommentsByParentId(UUID id);
}
