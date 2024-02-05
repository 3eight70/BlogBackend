package com.example.BlogBackend.Models.Post;

import com.example.BlogBackend.Models.Comment.CommentDto;
import com.example.BlogBackend.Models.Tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConcretePostInfoDto {
    private UUID id;

    private LocalDateTime createTime;

    private String title;

    private String description;

    private int readingTime;

    private String image;

    private UUID authorId;

    private String author;

    private UUID communityId;

    private String communityName;

    private UUID addressId;

    private int likes = 0;

    private boolean hasLike = false;

    private int commentsCount = 0;

    private List<TagDto> tags = new ArrayList<>();

    private List<CommentDto> comments = new ArrayList<>();

}
