package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Post.CreatePostDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/post")
public class PostController extends BaseController{
    @PostMapping
    public void createPost(CreatePostDto createPostDto){

    }
}
