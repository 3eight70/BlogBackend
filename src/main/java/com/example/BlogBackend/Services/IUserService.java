package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.User.UserDto;

public interface IUserService {
    UserDto GetUserProfile();
    boolean RegisterUser(UserDto user);
}
