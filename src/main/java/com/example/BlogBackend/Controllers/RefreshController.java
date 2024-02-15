package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Token.JwtResponse;
import com.example.BlogBackend.Models.Token.RefreshRequestDto;
import com.example.BlogBackend.Models.Token.RefreshToken;
import com.example.BlogBackend.Repositories.RedisRepository;
import com.example.BlogBackend.Services.IRefreshTokenService;
import com.example.BlogBackend.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.BlogBackend.Services.UserService.validToken;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshController {
    private final IRefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisRepository redisRepository;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto){
        try {
            return refreshTokenService.findByToken(refreshRequestDto.getToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(userInfo -> {
                        String accessToken = jwtTokenUtils.generateToken(userInfo);
                        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(accessToken), validToken);
                        return ResponseEntity.ok(JwtResponse.builder()
                                .accessToken(refreshRequestDto.getToken())
                                .token(accessToken).build());
                    }).orElseThrow(() -> new RuntimeException("Refresh Token не в базе данных"));
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }
    }
}
