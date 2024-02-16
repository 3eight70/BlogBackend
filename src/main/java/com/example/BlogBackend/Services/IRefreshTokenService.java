package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Token.RefreshToken;
import com.example.BlogBackend.Models.User.LoginCredentials;
import com.example.BlogBackend.Models.User.UserRegisterModel;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String email);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken checkRefreshToken(LoginCredentials authRequest);
    RefreshToken verifyExpiration(RefreshToken token);
}
