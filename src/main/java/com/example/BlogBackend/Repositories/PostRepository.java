package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Comment.Comment;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Models.User.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<FullPost, UUID> {
    FullPost findPostFullDtoById(UUID postId);

    List<FullPost> findFullPostsByAuthorId(UUID authorId);

    FullPost findByCommentsContains(Comment comment);

    @Query(value = "SELECT * FROM posts p " +
    "WHERE p.community_id IS NOT NULL " +
            "AND p.community_id = :communityId",
    nativeQuery = true)
    List<FullPost> findAllFullPostsInCommunity(
            @Param("communityId") UUID communityId);
}
