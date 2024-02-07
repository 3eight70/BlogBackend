package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Models.User.UserProfileDto;
import com.example.BlogBackend.Models.User.UserRegisterModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {
    public static UserProfileDto userDtoToUserProfile(User userDto){
        return new UserProfileDto(userDto.getId(),
                userDto.getCreateTime(),
                userDto.getFullName(),
                userDto.getBirthDate(),
                userDto.getGender(),
                userDto.getEmail(),
                userDto.getPhoneNumber());
    }

    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getFullName(),
                userRegisterModel.getBirthDate(),
                userRegisterModel.getGender(),
                userRegisterModel.getEmail(),
                userRegisterModel.getPhoneNumber(),
                userRegisterModel.getPassword());
    }

    public static UserDto userToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getCreateTime(),
                user.getFullName(),
                user.getBirthDate(),
                user.getGender(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

}
