package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Community.CreateCommunityDto;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Models.Post.PostSorting;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Services.CommunityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CreateCommunityDto createCommunityDto, @AuthenticationPrincipal User user) {
        try {
            return communityService.createCommunity(createCommunityDto, user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getCommunityList() {
        try {
            return communityService.getCommunities();
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/unsubscribe")
    public ResponseEntity<?> unsubscribeFromCommunity(@PathVariable("id") UUID id, @AuthenticationPrincipal User user) {
        try {
            return communityService.unsubscribe(user, id);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "У пользователя нет подписки на данное сообщество"), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> subscribeToCommunity(@PathVariable("id") UUID id, @AuthenticationPrincipal User user) {
        try {
            return communityService.subscribe(user, id);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "У пользователя уже есть подписка на данное сообщество"), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getUsersCommunityList(@AuthenticationPrincipal User user) {
        try {
            return communityService.getUsersCommunities(user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoAboutCommunity(@PathVariable UUID id) {
        try {
            return communityService.getInfoAboutCommunity(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<?> createPostInCommunity(@PathVariable UUID id, @RequestBody CreatePostDto createPostDto, @AuthenticationPrincipal User user) {
        try {
            return communityService.createPostInCommunity(createPostDto, id, user);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданных тэгов не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<?> getUserGreatestRoleInCommunity(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        try {
            return communityService.getUsersGreatestRole(id, user);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/post")
    public ResponseEntity<?> getCommunityPosts(@PathVariable UUID id, @AuthenticationPrincipal User user,
                                               @RequestParam(name = "tags", required = false) List<UUID> tags,
                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                               @RequestParam(name = "size", defaultValue = "5") int size,
                                               @RequestParam(name = "sortOrder", defaultValue = "CreateAsc") PostSorting sortOrder) {
        try {
            return communityService.getCommunityPosts(id, user, tags, page, size, sortOrder);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
