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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TagRepository tagRepository;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> createPost(CreatePostDto createPostDto, String email){
        UserDto user = userService.loadUserByUsername(email);
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
    public ResponseEntity<?> getPosts(String email){
        UserDto user = userService.loadUserByUsername(email);
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
    public ResponseEntity<?> addLikeToPost(String email, UUID postId){
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null){
            throw new EntityNotFoundException();
        }


        UserDto user = userService.loadUserByUsername(email);
        if (post.getLikesByUsers().contains(user)){
            throw new IllegalStateException();
        }

        post.setLikes(post.getLikes() + 1);

        post.getLikesByUsers().add(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteLikeFromPost(String email, UUID postId){
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null){
            throw new EntityNotFoundException();
        }

        UserDto user = userService.loadUserByUsername(email);
        if (!post.getLikesByUsers().contains(user)){
            throw new IllegalStateException();
        }

        post.setLikes(post.getLikes() - 1);

        post.getLikesByUsers().remove(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForAuthorized(String email, UUID postId){
        UserDto user = userService.loadUserByUsername(email);
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
