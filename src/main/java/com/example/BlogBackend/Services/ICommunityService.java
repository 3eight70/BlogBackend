package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Community.CreateCommunityDto;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Models.Post.PostSorting;
import com.example.BlogBackend.Models.User.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ICommunityService {
    ResponseEntity<?> getCommunities();
    ResponseEntity<?> createCommunity(CreateCommunityDto createCommunityDto, User user);
    ResponseEntity<?> subscribe(User user, UUID id);
    ResponseEntity<?> unsubscribe(User user, UUID id);
    ResponseEntity<?> getUsersCommunities(User user);
    ResponseEntity<?> getInfoAboutCommunity(UUID id);
    ResponseEntity<?> createPostInCommunity(CreatePostDto createPostDto, UUID id, User user);
    ResponseEntity<?> getUsersGreatestRole(UUID id, User user);
    ResponseEntity<?> getCommunityPosts(UUID id, User user, List<UUID> tags, int page, int size, PostSorting sortOrder);

}
