package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.User.*;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    User loadUserByUsername(String email);
    ResponseEntity<?> editUserProfile(UserEditProfileDto userEditProfileDto, User user);
    UserProfileDto getUserProfile(User user);
    ResponseEntity<?> logoutUser(String token);
    ResponseEntity<?> loginUser(LoginCredentials authRequest);
    ResponseEntity<?> registerUser(UserRegisterModel userRegisterModel);

}
