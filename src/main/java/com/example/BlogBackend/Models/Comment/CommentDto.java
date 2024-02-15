package com.example.BlogBackend.Models.Comment;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private UUID id;

    @NotNull
    private LocalDateTime createTime;

    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @NotNull
    private String content;

    private LocalDateTime modifiedDate;

    private LocalDateTime deleteDate;

    @NotNull
    private UUID authorId;

    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @NotNull
    private String author;

    @NotNull
    private int subComments;
}
