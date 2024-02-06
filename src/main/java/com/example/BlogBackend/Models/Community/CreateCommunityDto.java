package com.example.BlogBackend.Models.Community;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommunityDto {
    @NotNull
    @Size(min = 1, message = "Минимальная длина названия группы - 1")
    private String name;

    private String description;

    @NotNull
    private Boolean isClosed;
}
