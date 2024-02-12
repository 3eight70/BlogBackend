package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Community.Community;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Models.Post.PostSorting;
import com.example.BlogBackend.Models.User.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    ResponseEntity<?> createPost(CreatePostDto createPostDto, User user, Community community);
    ResponseEntity<?> getPosts(User user, List<UUID> tags, String authorName, PostSorting sortOrder,
                               Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                               Integer page, Integer size);
    ResponseEntity<?> getPostsForUnauthorizedUser(List<UUID> tags, String authorName, PostSorting sortOrder,
                                                  Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                                  Integer page, Integer size);
    ResponseEntity<?> addLikeToPost(User user, UUID postId);
    ResponseEntity<?> deleteLikeFromPost(User user, UUID postId);
    ResponseEntity<?> getInfoAboutConcretePostForAuthorized(User user, UUID postId);
    ResponseEntity<?> getInfoAboutConcretePostForUnauthorized(UUID postId);
}
