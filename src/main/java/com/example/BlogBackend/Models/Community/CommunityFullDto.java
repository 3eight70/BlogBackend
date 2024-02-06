package com.example.BlogBackend.Models.Community;

import com.example.BlogBackend.Models.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityFullDto {
    private UUID id;

    @NotNull
    private LocalDateTime createTime;

    @NotNull
    @Size(min = 1, message = "Минимальная длина названия группы - 1")
    private String name;

    private String description;

    @NotNull
    private Boolean isClosed = false;

    @NotNull
    private int subscribersCount = 0;

    @NotNull
    private int administratorsCount;

    @NotNull
    private List<User> administrators = new ArrayList<>();
}
