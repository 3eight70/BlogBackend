package com.example.BlogBackend.Models.User;

import com.example.BlogBackend.Models.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegisterModel {
    @Size(min = 1, max = 1000)
    private String fullName;

    @Size(min = 6)
    private String password;

    @Size(min=1)
    private String email;

    private LocalDateTime birthDate;

    @NotBlank(message = "Пользователь должен иметь пол")
    private Gender gender;

    private String phoneNumber;
}
