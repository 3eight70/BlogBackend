package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Community.CommunityDto;
import com.example.BlogBackend.Models.Community.CreateCommunityDto;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Services.CommunityService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/community")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CreateCommunityDto createCommunityDto, @AuthenticationPrincipal User user) {
        try {
            return communityService.createCommunity(createCommunityDto, user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/community")
    public ResponseEntity<?> getCommunityList() {
        try {
            return communityService.getCommunities();
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/community/{id}/unsubscribe")
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

    @PostMapping("/community/{id}/subscribe")
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
}
