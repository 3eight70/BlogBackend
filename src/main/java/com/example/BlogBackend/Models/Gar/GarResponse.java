package com.example.BlogBackend.Models.Gar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GarResponse {
    private Long objectId;

    private UUID objectGuid;

    private String text;

    public GarAddressLevel objectLevel;

    public String objectLevelText;
}
