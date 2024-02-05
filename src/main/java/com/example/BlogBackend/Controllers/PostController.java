package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Services.PostService;
import com.example.BlogBackend.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController{
    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostDto createPostDto, HttpServletRequest request){
        String token = TokenUtils.getToken(request);

        try{
            return postService.createPost(createPostDto, token);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного тэга не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPosts(HttpServletRequest request){
        String token = TokenUtils.getToken(request);

        try {
            if (token != null) {
                return postService.getPosts(token);
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
    public ResponseEntity<?> deleteLike(@PathVariable("postId") UUID id, HttpServletRequest request){
        String token = TokenUtils.getToken(request);

        try{
            return postService.deleteLikeFromPost(token, id);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLike(@PathVariable("postId") UUID id, HttpServletRequest request){
        String token = TokenUtils.getToken(request);

        try{
            return postService.addLikeToPost(token, id);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданного поста не существует"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getInfoAboutConcretePost(@PathVariable("id") UUID id, HttpServletRequest request){
        String token = TokenUtils.getToken(request);

        try{
            if (token != null) {
                return postService.getInfoAboutConcretePostForAuthorized(token, id);
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
