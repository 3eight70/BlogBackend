package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Post.PostFullDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostFullDto, UUID> {
}
