package com.example.BlogBackend.Models.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDto {
    private UUID id;

    private LocalDateTime createTime;

    private String name;

    private String description;

    private Boolean isClosed;

    private int subscribersCount;

    private int administratorsCount;
}
