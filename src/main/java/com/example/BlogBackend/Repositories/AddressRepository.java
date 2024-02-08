package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Gar.AsAddrObj;
import com.example.BlogBackend.Models.Gar.AsHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<AsAddrObj, Long> {
    default AsAddrObj findByObjectidAndIsactiveAndIsactual(Long objectid) {
        return findByObjectidAndIsactiveAndIsactual(objectid, 1, 1);
    }

    AsAddrObj findByObjectidAndIsactiveAndIsactual(Long objectid,
                                                          int isactive,
                                                          int isactual);

    default AsAddrObj findByObjectguidAndIsactiveAndIsactual(UUID objectguid) {
        return findByObjectguidAndIsactiveAndIsactual(objectguid, 1, 1);
    }

    AsAddrObj findByObjectguidAndIsactiveAndIsactual(UUID objectguid,
                                                   int isactive,
                                                   int isactual);
}
