package com.example.BlogBackend.Models.Exceptions;
import lombok.Data;

@Data
public class ExceptionResponse {
    private int status;
    private String message;

    public ExceptionResponse(int status, String message){
        this.status = status;
        this.message = message;
    }
}
