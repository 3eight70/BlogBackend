package com.example.BlogBackend.Models.User;

import com.example.BlogBackend.Models.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private LocalDateTime createTime;
    private String fullName;
    private LocalDateTime birthDate;
    private Gender gender;
    private String email;
    private String phoneNumber;
}
