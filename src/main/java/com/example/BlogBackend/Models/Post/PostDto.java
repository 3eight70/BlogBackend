package com.example.BlogBackend.Models.Post;

import com.example.BlogBackend.Models.Tag.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PostDto {
    private UUID id;

    @NotNull
    private LocalDateTime createTime;

    @NotNull
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String title;

    @NotNull
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String description;

    @NotNull
    private int readingTime;

    private String image;

    @NotNull
    private UUID authorId;

    @NotNull
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String author;

    private UUID communityId;

    private String communityName;

    private UUID addressId;

    @NotNull
    private int likes = 0;

    @NotNull
    private boolean hasLike;

    @NotNull
    private int commentsCount = 0;

    private List<Tag> tags = new ArrayList<>();


}
