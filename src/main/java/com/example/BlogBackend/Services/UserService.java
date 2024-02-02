package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto GetUserProfile(){
        return new UserDto();
    }

    public boolean RegisterUser(UserDto user){
        String email = user.getEmail();

        if (userRepository.findByEmail(email) != null) {
            return false;
        }
        log.info("Saving new user with email {}", email);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }
}
