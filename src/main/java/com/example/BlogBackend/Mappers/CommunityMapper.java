package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.Community.*;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Models.User.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommunityMapper {
    public static Community createCommunityDtoToCommunity(CreateCommunityDto communityDto) {
        return new Community(
                UUID.randomUUID(),
                LocalDateTime.now(),
                communityDto.getName(),
                communityDto.getDescription(),
                communityDto.getIsClosed(),
                0,
                0,
                new ArrayList<>(),
                new ArrayList<>());
    }

    public static CommunityDto communityToCommunityDto(Community community) {
        return new CommunityDto(
                community.getId(),
                community.getCreateTime(),
                community.getName(),
                community.getDescription(),
                community.getIsClosed(),
                community.getSubscribersCount(),
                community.getAdministratorsCount());
    }

    public static CommunityUserDto communityToCommunityUserDto(Community community, User user, CommunityRole communityRole) {
        return new CommunityUserDto(user.getId(), community.getId(), communityRole);
    }

    public static CommunityFullDto communityToCommunityFullDto(Community community, List<UserDto> administrators) {
        return new CommunityFullDto(
                community.getId(),
                community.getCreateTime(),
                community.getName(),
                community.getDescription(),
                community.getIsClosed(),
                community.getSubscribersCount(),
                community.getAdministratorsCount(),
                administrators);
    }
}
