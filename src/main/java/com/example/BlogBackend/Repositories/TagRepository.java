package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Tag.TagDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<TagDto, UUID> {
}
