package com.example.BlogBackend.Models.User;

import com.example.BlogBackend.Models.enums.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEditProfileDto {
    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String fullName;

    @Column(unique = true, nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Invalid email address")
    private String email;

    private LocalDateTime birthDate;

    @NotNull(message = "Пользователь должен иметь пол")
    private Gender gender;

    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$", message = "Invalid phone number")
    private String phoneNumber;
}
