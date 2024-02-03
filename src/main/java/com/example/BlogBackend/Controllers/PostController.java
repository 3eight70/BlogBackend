package com.example.BlogBackend.Controllers;

import com.example.BlogBackend.Models.Post.CreatePostDto;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/post")
public class PostController extends BaseController{

    @GetMapping
    public String get(){
        return "123";
    }
    @PostMapping
    public void createPost(CreatePostDto createPostDto){

    }

    @DeleteMapping("/{postId}/like")
    public void deleteLike(@PathVariable("postId") UUID id){

    }
}
