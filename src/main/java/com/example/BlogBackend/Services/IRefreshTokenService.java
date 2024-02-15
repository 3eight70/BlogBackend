package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Token.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String email);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
}
