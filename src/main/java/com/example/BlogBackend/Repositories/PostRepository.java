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
            "JOIN post_tag pt on p.id = pt.post_id " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND p.community_id IN ( " +
            "SELECT community_id FROM community_subscribers WHERE subscriber_id = :userId UNION " +
            "SELECT community_id FROM community_administrators WHERE administrator_id = :userId) " +
            "AND EXISTS (SELECT 1 FROM communities WHERE id = p.community_id) " +
            "AND (pt.tag_id IN :tags)",
            nativeQuery = true)
    List<FullPost> findOnlyCommunitiesPostsWithTags(@Param("authorName") String authorName,
                                            @Param("min") Integer min,
                                            @Param("max") Integer max,
                                                    @Param("tags") List<UUID> tags,
                                            @Param("userId") UUID userId,
                                            Pageable pageable);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND p.community_id IN ( " +
            "SELECT community_id FROM community_subscribers WHERE subscriber_id = :userId UNION " +
            "SELECT community_id FROM community_administrators WHERE administrator_id = :userId) " +
            "AND EXISTS (SELECT 1 FROM communities WHERE id = p.community_id)",
            nativeQuery = true)
    List<FullPost> findOnlyCommunitiesPostsWithoutTags(@Param("authorName") String authorName,
                                                    @Param("min") Integer min,
                                                    @Param("max") Integer max,
                                                    @Param("userId") UUID userId,
                                                    Pageable pageable);

    @Query(value = "SELECT * FROM posts p " +
            "JOIN post_tag pt ON p.id = pt.post_id " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND (:onlyMyCommunities = false OR p.community_id IN (SELECT id FROM communities)) " +
            "AND (p.community_id IN (SELECT id FROM communities WHERE is_closed = false) " +
            "OR (p.community_id IN ( " +
            "SELECT community_id FROM community_subscribers WHERE subscriber_id = :userId UNION " +
            "SELECT community_id FROM community_administrators WHERE administrator_id = :userId) " +
            "AND EXISTS (SELECT 1 FROM communities WHERE id = p.community_id AND is_closed = true))" +
            "OR (p.community_id IS NULL) " +
            "AND (pt.tag_id IN :tags))",
            nativeQuery = true)
    List<FullPost> findPostsByParametersWithTagsIfUserNotNull(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
            @Param("tags") List<UUID> tags,
            @Param("userId") UUID userId,
            Pageable pageable
    );


    @Query(value = "SELECT * FROM posts p " +
            "JOIN post_tag pt ON p.id = pt.post_id " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND (p.community_id IN (SELECT id FROM communities WHERE is_closed = false) " +
            "OR p.community_id IS NULL) " +
            "AND (pt.tag_id IN :tags)",
            nativeQuery = true)
    List<FullPost> findPostsByParametersWithTagsIfUserNull(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
            @Param("tags") List<UUID> tags,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM posts p " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND (p.community_id IN (SELECT id FROM communities WHERE is_closed = false) " +
            "OR (p.community_id IN ( " +
            "SELECT community_id FROM community_subscribers WHERE subscriber_id = :userId UNION " +
            "SELECT community_id FROM community_administrators WHERE administrator_id = :userId) " +
            "AND EXISTS (SELECT 1 FROM communities WHERE id = p.community_id AND is_closed = true)) " +
            "OR p.community_id IS NULL)" ,
            nativeQuery = true)
    List<FullPost> findPostsByParametersWithoutTagsIfUserNotNull(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM posts p " +
            "WHERE (:authorName IS NULL OR p.author = :authorName) " +
            "AND (:min IS NULL OR p.reading_time >= :min) " +
            "AND (:max IS NULL OR p.reading_time <= :max) " +
            "AND (p.community_id IN (SELECT id FROM communities WHERE is_closed = false) " +
            "OR p.community_id IS NULL)",
            nativeQuery = true)
    List<FullPost> findPostsByParametersWithoutTagsIfUserNull(
            @Param("authorName") String authorName,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM posts p " +
            "JOIN post_tag pt ON pt.post_id = p.id " +
            "WHERE p.community_id = :communityId " +
            "AND (pt.tag_id IN :tags)",
            nativeQuery = true)
    List<FullPost> findCommunityPostsWithTags(
            @Param("tags") List<UUID> tags,
            @Param("communityId") UUID communityId,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.community_id = :communityId",
            nativeQuery = true)
    List<FullPost> findCommunityPostsWithoutTags(
            @Param("communityId") UUID communityId,
            Pageable pageable
    );
}
