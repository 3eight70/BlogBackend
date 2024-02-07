package com.example.BlogBackend.Models.Comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    @NotNull
    @Size(min = 1, max = 1000)
    private String content;

    private UUID parentId;
}
