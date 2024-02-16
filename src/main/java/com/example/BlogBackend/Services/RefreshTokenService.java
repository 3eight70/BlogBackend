package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.User.LoginCredentials;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Models.User.UserRegisterModel;
import com.example.BlogBackend.Repositories.RefreshTokenRepository;
import com.example.BlogBackend.Repositories.UserRepository;
import com.example.BlogBackend.Models.Token.RefreshToken;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService{
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${refresh.expiration}")
    private Duration lifetime;

    @Transactional
    public RefreshToken createRefreshToken(String email){

        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(lifetime))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }



    @Transactional
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken checkRefreshToken(LoginCredentials authRequest){
        User user = userRepository.findByEmail(authRequest.getEmail());
        Optional<RefreshToken> refreshOptional = refreshTokenRepository.findByUserId(user.getId())
                .map(token -> verifyExpiration(token));

        if (refreshOptional.isPresent()) {
            RefreshToken refreshToken = refreshOptional.get();
            refreshTokenRepository.delete(refreshToken);
        }

        return createRefreshToken(authRequest.getEmail());
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " действие RefreshToken'а истекло");
        }
        return token;
    }
}
