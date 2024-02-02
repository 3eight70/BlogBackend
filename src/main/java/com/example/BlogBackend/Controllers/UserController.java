package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.User.LoginCredentials;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class UserController  extends BaseController{
    private final IUserService userService;

    @PostMapping("/register")
    public void register(UserDto user){

    }

    @PostMapping("/login")
    public void login(LoginCredentials data){

    }

    @PostMapping("/logout")
    public void logout(){

    }

    @GetMapping("/profile")
    public void getProfile(){

    }

    @PutMapping("/profile")
    public void editProfile(){

    }
}
