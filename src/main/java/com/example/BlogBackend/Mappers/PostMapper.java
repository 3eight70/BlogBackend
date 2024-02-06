package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.Post.ConcretePostInfoDto;
import com.example.BlogBackend.Models.Post.PostDto;
import com.example.BlogBackend.Models.Post.FullPost;

public class PostMapper {
    public static PostDto postFullDtoToPostDto(FullPost postFullDto){
        return new PostDto(postFullDto.getId(),
                postFullDto.getCreateTime(),
                postFullDto.getTitle(),
                postFullDto.getDescription(),
                postFullDto.getReadingTime(),
                postFullDto.getImage(),
                postFullDto.getAuthorId(),
                postFullDto.getAuthor(),
                postFullDto.getCommunityId(),
                postFullDto.getCommunityName(),
                postFullDto.getAddressId(),
                postFullDto.getLikes(),
                postFullDto.isHasLike(),
                postFullDto.getCommentsCount(),
                postFullDto.getTags());
    }

    public static ConcretePostInfoDto postFullDtoToConcretePostDto(FullPost postFullDto){
        return new ConcretePostInfoDto(postFullDto.getId(),
                postFullDto.getCreateTime(),
                postFullDto.getTitle(),
                postFullDto.getDescription(),
                postFullDto.getReadingTime(),
                postFullDto.getImage(),
                postFullDto.getAuthorId(),
                postFullDto.getAuthor(),
                postFullDto.getCommunityId(),
                postFullDto.getCommunityName(),
                postFullDto.getAddressId(),
                postFullDto.getLikes(),
                postFullDto.isHasLike(),
                postFullDto.getCommentsCount(),
                postFullDto.getTags(),
                postFullDto.getComments());
    }
}
