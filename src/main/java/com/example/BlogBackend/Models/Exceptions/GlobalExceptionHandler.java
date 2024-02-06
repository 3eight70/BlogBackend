package com.example.BlogBackend.Models.Exceptions;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Ошибка валидации";
        int status = HttpStatus.BAD_REQUEST.value();
        return new ResponseEntity<>(new ExceptionResponse(status, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return new ResponseEntity<>(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
    }
}
