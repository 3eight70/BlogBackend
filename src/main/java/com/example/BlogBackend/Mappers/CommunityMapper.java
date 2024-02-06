package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.Community.Community;
import com.example.BlogBackend.Models.Community.CommunityDto;
import com.example.BlogBackend.Models.Community.CreateCommunityDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class CommunityMapper {
    public static Community createCommunityDtoToFullCommunityDto(CreateCommunityDto communityDto){
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

    public static CommunityDto communityToCommunityDto(Community community){
        return new CommunityDto(
                community.getId(),
                community.getCreateTime(),
                community.getName(),
                community.getDescription(),
                community.getIsClosed(),
                community.getSubscribersCount(),
                community.getAdministratorsCount());
    }
}
