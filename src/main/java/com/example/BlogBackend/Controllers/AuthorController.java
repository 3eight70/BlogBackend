package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Services.IAuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/author")
public class AuthorController {
    private final IAuthorService authorService;

    @GetMapping("/list")
    public ResponseEntity<?> getAuthorList() {
        try {
            return authorService.getAuthorsList();
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
