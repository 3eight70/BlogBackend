package com.example.BlogBackend.Models.Tag;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTagDto {
    @NotNull
    private String name;
}

