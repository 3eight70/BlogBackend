package com.example.BlogBackend.Models.Post;

import com.example.BlogBackend.Models.Tag.TagDto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostDto {
    @Size(min = 5, max=1000, message = "Название должно быть не менее 5 символов")
    @NotNull
    private String title;

    @Size(min = 5, max=5000, message = "Описание должно быть не менее 5 символов")
    @NotNull
    private String description;

    @NotNull
    @Min(value = 0, message = "Время чтения должно быть не менее 0")
    private int readingTime;

    private String image;

    private UUID addressId;

    private List<UUID> tags = new ArrayList<>();
}
