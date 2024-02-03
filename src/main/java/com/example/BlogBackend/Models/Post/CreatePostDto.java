package com.example.BlogBackend.Models.Post;

import com.example.BlogBackend.Models.TagDto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostDto {
    @Size(min = 5, max=1000)
    @Column(nullable = false)
    private String title;

    @Size(min = 5, max=5000)
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int readingTime;

    private String image;

    private UUID addressId;

    private List<TagDto> tags = new ArrayList<>();
}
