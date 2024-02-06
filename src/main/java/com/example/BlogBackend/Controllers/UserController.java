package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.User.*;
import com.example.BlogBackend.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class UserController extends BaseController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserRegisterModel userRegisterModel){
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword()));

        try{
            return userService.registerUser(userRegisterModel);
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Данные введены некорректно"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCredentials authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }

        return userService.loginUser(authRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token){
        try{
            return userService.logoutUser(token);
        } catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal){
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@Valid @RequestBody UserEditProfileDto userEditProfileDto, Principal principal){
        try{
            return userService.editUserProfile(userEditProfileDto, principal.getName());
        }
        catch (Exception e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
