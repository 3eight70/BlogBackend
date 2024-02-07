package com.example.BlogBackend.Models.Author;

import com.example.BlogBackend.Models.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private String fullName;

    private LocalDateTime birthDate;

    private Gender gender;

    private int posts;

    private int likes;

    private LocalDateTime created;
}
