package com.example.BlogBackend.configurations;

import com.example.BlogBackend.Repositories.RedisRepository;
import com.example.BlogBackend.Services.UserService;
import com.example.BlogBackend.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final RedisRepository redisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        boolean tokenInRedis = false;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            if (redisRepository.checkToken(jwtTokenUtils.getIdFromToken(jwt))){
                tokenInRedis = true;
            }

            try {
                email = jwtTokenUtils.getUserEmail(jwt);
            } catch (ExpiredJwtException e) {
                log.debug("Время жизни токена истекло");
            } catch (SignatureException e) {
                log.debug("Неверная подпись");
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && tokenInRedis) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    userService.loadUserByUsername(email).getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
