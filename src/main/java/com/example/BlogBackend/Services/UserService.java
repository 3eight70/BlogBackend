package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.UserMapper;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Models.User.UserProfileDto;
import com.example.BlogBackend.Models.User.UserRegisterModel;
import com.example.BlogBackend.Repositories.UserRepository;
import com.example.BlogBackend.utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    @Transactional
    public UserDto loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public UserProfileDto getUserProfile(String token) {
        return UserMapper.userDtoToUserProfile(jwtTokenUtils.getUserFromToken(token));
    }

    @Transactional
    public ResponseEntity<?> registerUser(UserRegisterModel userRegisterModel) {
        UserDto user = new UserDto(UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getFullName(),
                userRegisterModel.getBirthDate(),
                userRegisterModel.getGender(),
                userRegisterModel.getEmail(),
                userRegisterModel.getPhoneNumber(),
                userRegisterModel.getPassword());

        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с такой почтой уже существует"), HttpStatus.BAD_REQUEST);
        }

        log.info("Saving new user with email {}", email);

        userRepository.save(user);

        return ResponseEntity.ok(jwtTokenUtils.generateToken(user));
    }
}
