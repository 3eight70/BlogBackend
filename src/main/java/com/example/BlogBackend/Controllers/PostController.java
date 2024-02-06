package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Services.PostService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController{
    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostDto createPostDto, Principal principal){
        try{
            return postService.createPost(createPostDto, principal.getName());
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного тэга не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPosts(Principal principal){
        try {
            if (principal != null && principal.getName() != null) {
                return postService.getPosts(principal.getName());
            }
            else{
                return postService.getPostsForUnauthorizedUser();
            }
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена авторизации истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> deleteLike(@PathVariable("postId") UUID id, Principal principal){
        try{
            return postService.deleteLikeFromPost(principal.getName(), id);
        }
        catch (IllegalStateException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "У пользователя нет лайка на данном посте"), HttpStatus.BAD_REQUEST);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLike(@PathVariable("postId") UUID id, Principal principal){
        try{
            return postService.addLikeToPost(principal.getName(), id);
        }
        catch (IllegalStateException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "У пользователя уже есть лайк на данном посте"), HttpStatus.BAD_REQUEST);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getInfoAboutConcretePost(@PathVariable("id") UUID id, Principal principal){
        try{
            if (principal != null && principal.getName() != null) {
                return postService.getInfoAboutConcretePostForAuthorized(principal.getName(), id);
            }
            else{
                return postService.getInfoAboutConcretePostForUnauthorized(id);
            }
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
