package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Gar.AsAddrObj;
import com.example.BlogBackend.Models.Gar.AsHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HouseRepository extends JpaRepository<AsHouse, Long> {
    default AsHouse findByObjectidAndIsactiveAndIsactual(Long objectid) {
        return findByObjectidAndIsactiveAndIsactual(objectid, 1, 1);
    }

    AsHouse findByObjectidAndIsactiveAndIsactual(Long objectid,
                                                   int isactive,
                                                   int isactual);

    default AsHouse findByObjectguidAndIsactiveAndIsactual(UUID objectguid) {
        return findByObjectguidAndIsactiveAndIsactual(objectguid, 1, 1);
    }

    AsHouse findByObjectguidAndIsactiveAndIsactual(UUID objectguid,
                                                 int isactive,
                                                 int isactual);
}
