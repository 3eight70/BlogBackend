package com.example.BlogBackend.Mappers;

import com.example.BlogBackend.Models.Gar.*;

public class GarMapper {
    public static GarResponse addressToGarResponse(AsAddrObj address){
        return new GarResponse(
                address.getObjectid(),
                address.getObjectguid(),
                address.getTypename() + " " + address.getName(),
                null,
                address.getLevel()
        );
    }

    public static GarResponse houseToGarResponse(AsHouse house){
        return new GarResponse(
                house.getObjectid(),
                house.getObjectguid(),
                house.getHousenum(),
                null,
                "10"
        );
    }
}
