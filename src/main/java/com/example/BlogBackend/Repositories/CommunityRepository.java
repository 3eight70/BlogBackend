package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Community.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID> {
    Community findByName(String name);
    Community findCommunityById(UUID id);
}
