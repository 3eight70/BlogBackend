package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Post.FullPost;
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

    @Query(value = "SELECT * FROM posts p " +
            "JOIN post_tag pt ON p.id = pt.post_id " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
           // "AND (:onlyMyCommunities IS NULL)" +
            "AND (:tags IS NULL OR pt.tag_id IN :tags)",
    nativeQuery = true)
    List<FullPost> findPostsByParametersWithTags(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
           // @Param("onlyMyCommunities") Boolean onlyMyCommunities,
            @Param("tags") List<UUID> tags,
            Pageable pageable
    );


    @Query(value = "SELECT * FROM posts p " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) ",
            // "AND (:onlyMyCommunities IS NULL)" +
            nativeQuery = true)
    List<FullPost> findPostsByParametersWithoutTags(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
            // @Param("onlyMyCommunities") Boolean onlyMyCommunities,
    );
}
