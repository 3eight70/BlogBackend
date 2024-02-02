package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.Post.PostDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostDto, Long> {
}
