package com.example.BlogBackend.Services;

import org.springframework.http.ResponseEntity;

public interface IAuthorService {
    ResponseEntity<?> getAuthorsList();
}
