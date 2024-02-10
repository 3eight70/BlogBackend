package com.example.BlogBackend.configurations;

import com.example.BlogBackend.Models.User.User;
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

        response.setHeader("Access-Control-Allow-Origin", "http://localhost");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, X-Auth-Token");

        try {
            if (authHeader != null && authHeader.equals("Bearer null")) {
                authHeader = null;
            }

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);

                if (redisRepository.checkToken(jwtTokenUtils.getIdFromToken(jwt))) {
                    tokenInRedis = true;
                }
                email = jwtTokenUtils.getUserEmail(jwt);
            }
        } catch (ExpiredJwtException e) {
            log.debug("Токен просрочен");
        } catch (SignatureException e) {
            log.debug("Неверная подпись");
        }

        User user = userService.loadUserByUsername(email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && tokenInRedis) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
