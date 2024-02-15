package com.example.BlogBackend.Models.Community;

import com.example.BlogBackend.Models.User.UserDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private int administratorsCount = 0;

    @NotNull
    private List<UserDto> administrators = new ArrayList<>();
}
