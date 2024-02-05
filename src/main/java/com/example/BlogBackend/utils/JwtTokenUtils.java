package com.example.BlogBackend.utils;

import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Repositories.RedisRepository;
import com.example.BlogBackend.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Duration lifetime;

    public String generateToken(UserDto user){
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        claims.put("roles", rolesList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());
        UUID tokenId = UUID.randomUUID();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .setId(tokenId.toString())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public UserDto getUserFromToken(String token){
        var userId = getAllClaimsFromToken(token).get("userId", String.class);
        return userRepository.findById(UUID.fromString(userId)).orElseThrow();
    }

    public String getUserEmail(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public void saveToken(String key, String value){
        redisRepository.save(key, value, lifetime.toMillis());
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token){
        try {
            if (redisRepository.checkToken(token)){
                return true;
            }
        }
        catch (Exception e){
            return false;
        }

        return false;
    }
}
