package com.example.BlogBackend.utils;

import jakarta.servlet.http.HttpServletRequest;

public class TokenUtils {
    public static String getToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        String token = null;

        if (header != null){
            token = header.substring(7);
        }

        return token;
    }
}
