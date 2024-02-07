package com.example.BlogBackend.Models.User;

import com.example.BlogBackend.Models.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;

    private LocalDateTime createTime;

    private String fullName;

    private LocalDateTime birthDate;

    private Gender gender;
    
    private String email;

    private String phoneNumber;
}
