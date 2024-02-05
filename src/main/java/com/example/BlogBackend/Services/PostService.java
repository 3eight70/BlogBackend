package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.PostMapper;
import com.example.BlogBackend.Models.Post.ConcretePostInfoDto;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Models.Post.PostDto;
import com.example.BlogBackend.Models.Post.PostFullDto;
import com.example.BlogBackend.Models.Tag.TagDto;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Repositories.PostRepository;
import com.example.BlogBackend.Repositories.TagRepository;
import com.example.BlogBackend.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService{
    private final PostRepository postRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final TagRepository tagRepository;

    @Transactional
    public ResponseEntity<?> createPost(CreatePostDto createPostDto, String token){
        UserDto user = jwtTokenUtils.getUserFromToken(token);
        PostFullDto post = new PostFullDto();
        List<TagDto> tags = tagRepository.findAllById(createPostDto.getTags());

        if (tags.size() != createPostDto.getTags().size()) {
            throw new EntityNotFoundException();
        }

        post.setAuthor(user.getFullName());
        post.setAuthorId(user.getId());
        post.setTitle(createPostDto.getTitle());
        post.setAddressId(createPostDto.getAddressId());
        post.setDescription(createPostDto.getDescription());
        post.setReadingTime(createPostDto.getReadingTime());
        post.setImage(createPostDto.getImage());
        post.setTags(tags);
        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getPosts(String token){
        UserDto user = jwtTokenUtils.getUserFromToken(token);
        List<PostDto> posts = getPostsByUser(user);
        return ResponseEntity.ok(posts);
    }

    @Transactional
    public ResponseEntity<?> getPostsForUnauthorizedUser(){
        List<PostDto> posts = getPostsByUser(null);
        return ResponseEntity.ok(posts);
    }

    private List<PostDto> getPostsByUser(UserDto user) {
        List<PostFullDto> fullPosts = postRepository.findAll();
        return checkLikes(fullPosts, user);
    }

    private List<PostDto> checkLikes(List<PostFullDto> fullPosts, UserDto user){
        List<PostDto> posts = new ArrayList<>();

        for (PostFullDto post : fullPosts){
            setLike(user, post);
            posts.add(PostMapper.postFullDtoToPostDto(post));
        }

        return posts;
    }

    private void setLike(UserDto user, PostFullDto post){
        post.setHasLike(false);

        if (user != null && post.getLikesByUsers().contains(user)) {
            post.setHasLike(true);
        }
    }


    @Transactional
    public ResponseEntity<?> addLikeToPost(String token, UUID postId){
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null){
            throw new EntityNotFoundException();
        }

        UserDto user = jwtTokenUtils.getUserFromToken(token);

        post.getLikesByUsers().add(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteLikeFromPost(String token, UUID postId){
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null){
            throw new EntityNotFoundException();
        }

        UserDto user = jwtTokenUtils.getUserFromToken(token);

        post.getLikesByUsers().remove(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForAuthorized(String token, UUID postId){
        UserDto user = jwtTokenUtils.getUserFromToken(token);
        return ResponseEntity.ok(getInfoAboutPost(user, postId));
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForUnauthorized(UUID postId){
        return ResponseEntity.ok(getInfoAboutPost(null, postId));
    }

    public ConcretePostInfoDto getInfoAboutPost(UserDto user, UUID postId){
        PostFullDto post = postRepository.findPostFullDtoById(postId);

        if (post == null){
            throw new EntityNotFoundException();
        }

        setLike(user, post);
        return PostMapper.postFullDtoToConcretePostDto(post);
    }
}
