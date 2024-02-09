package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Gar.AsAddrObj;
import com.example.BlogBackend.Models.Gar.AsHouse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.List;

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

    default List<AsAddrObj> findAllByIsactiveAndIsactual() {
        return findAllByIsactiveAndIsactual(1, 1);
    }

    List<AsAddrObj> findAllByIsactiveAndIsactual(
                                                   int isactive,
                                                   int isactual);
}
