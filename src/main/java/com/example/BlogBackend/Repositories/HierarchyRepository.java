package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Gar.AsAdmHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HierarchyRepository extends JpaRepository<AsAdmHierarchy, Long> {
    AsAdmHierarchy findByObjectid(Long objectid);

    List<AsAdmHierarchy> findAllByParentobjid(Long parentObjectId);
}
