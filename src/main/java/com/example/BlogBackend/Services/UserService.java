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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService,IUserService{
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisRepository redisRepository;

    private static final String validToken = "Valid";

    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> editUserProfile(UserEditProfileDto userEditProfileDto, User user) {
        if (userEditProfileDto.getBirthDate().isAfter(LocalDateTime.now())) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Дата рождения выставлена в будущем времени"), HttpStatus.BAD_REQUEST);
        }
        user.setEmail(userEditProfileDto.getEmail());
        user.setGender(userEditProfileDto.getGender());
        user.setFullName(userEditProfileDto.getFullName());
        user.setBirthDate(userEditProfileDto.getBirthDate());
        user.setPhoneNumber(userEditProfileDto.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public UserProfileDto getUserProfile(User user) {
        return UserMapper.userDtoToUserProfile(user);
    }

    @Transactional
    public ResponseEntity<?> logoutUser(String token) {
        String tokenId = "";

        if (token != null) {
            token = token.substring(7);
            tokenId = jwtTokenUtils.getIdFromToken(token);
        }
        redisRepository.delete(tokenId);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> loginUser(LoginCredentials authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail());
        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), validToken);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Transactional
    public ResponseEntity<?> registerUser(UserRegisterModel userRegisterModel) {
        if (userRegisterModel.getBirthDate().isAfter(LocalDateTime.now())) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Дата рождения выставлена в будущем времени"), HttpStatus.BAD_REQUEST);
        }
        User user = UserMapper.userRegisterModelToUser(userRegisterModel);

        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с такой почтой уже существует"), HttpStatus.BAD_REQUEST);
        }

        log.info("Saving new user with email {}", email);

        userRepository.save(user);
        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), validToken);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
