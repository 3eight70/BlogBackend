package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Models.User.UserProfileDto;

public class UserMapper {
    public static UserProfileDto userDtoToUserProfile(UserDto userDto){
        return new UserProfileDto(userDto.getId(),
                userDto.getCreateTime(),
                userDto.getFullName(),
                userDto.getBirthDate(),
                userDto.getGender(),
                userDto.getEmail(),
                userDto.getPhoneNumber());
    }
}
