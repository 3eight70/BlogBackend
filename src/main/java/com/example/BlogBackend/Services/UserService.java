package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.UserMapper;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.User.*;
import com.example.BlogBackend.Repositories.RedisRepository;
import com.example.BlogBackend.Repositories.UserRepository;
import com.example.BlogBackend.utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final RedisRepository redisRepository;

    private static final String validToken = "Valid";

    @Override
    @Transactional
    public UserDto loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> editUserProfile(UserEditProfileDto userEditProfileDto, String token){
        UserDto user = jwtTokenUtils.getUserFromToken(token);

        user.setEmail(userEditProfileDto.getEmail());
        user.setGender(userEditProfileDto.getGender());
        user.setFullName(userEditProfileDto.getFullName());
        user.setBirthDate(userEditProfileDto.getBirthDate());
        user.setPhoneNumber(userEditProfileDto.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    public UserProfileDto getUserProfile(String token) {
        return UserMapper.userDtoToUserProfile(jwtTokenUtils.getUserFromToken(token));
    }

    public ResponseEntity<?> logoutUser(String token){
        redisRepository.delete(token);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> loginUser(LoginCredentials authRequest){
        UserDto user = loadUserByUsername(authRequest.getEmail());
        String token = jwtTokenUtils.generateToken(user);
        jwtTokenUtils.saveToken(token, validToken);

        return ResponseEntity.ok(new JwtResponse(token));
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
        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(token, validToken);
        return ResponseEntity.ok(token);
    }
}
