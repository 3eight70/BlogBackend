package com.example.BlogBackend.Models.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityUserDto {
    private UUID userId;

    private UUID communityId;

    private CommunityRole role;
}
