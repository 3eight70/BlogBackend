package com.example.BlogBackend.Services;

import com.example.BlogBackend.Repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService{
    private final PostRepository postRepository;
}
