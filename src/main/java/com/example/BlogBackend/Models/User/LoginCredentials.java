package com.example.BlogBackend.Models.User;

import lombok.Data;

@Data
public class LoginCredentials {
    private String email;
    private String password;
}
