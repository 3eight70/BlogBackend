package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Token.JwtResponse;
import com.example.BlogBackend.Models.Token.RefreshToken;
import com.example.BlogBackend.Models.User.LoginCredentials;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Models.User.UserEditProfileDto;
import com.example.BlogBackend.Models.User.UserRegisterModel;
import com.example.BlogBackend.Repositories.RefreshTokenRepository;
import com.example.BlogBackend.Services.IRefreshTokenService;
import com.example.BlogBackend.Services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.BlogBackend.Services.UserService.validToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class UserController {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserRegisterModel userRegisterModel) {
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword()));

        try {
            return userService.registerUser(userRegisterModel);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Данные введены некорректно"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCredentials authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            if (authentication.isAuthenticated()){
                User user = userService.loadUserByUsername(authRequest.getEmail());
                Optional<RefreshToken> refresh = refreshTokenRepository.findByUserId(user.getId())
                        .map(refreshTokenService::verifyExpiration);

                if (refresh.isPresent()) {
                    return new ResponseEntity<>(new ExceptionResponse(HttpStatus.CONFLICT.value(), "У пользователя уже существует действующий refresh token"), HttpStatus.CONFLICT);
                }

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());

                return userService.loginUser(authRequest, refreshToken);
            }
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            return userService.logoutUser(token);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@Valid @RequestBody UserEditProfileDto userEditProfileDto, @AuthenticationPrincipal User user) {
        try {
            return userService.editUserProfile(userEditProfileDto, user);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
