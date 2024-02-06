package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.PostMapper;
import com.example.BlogBackend.Models.Post.*;
import com.example.BlogBackend.Models.Tag.TagDto;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Repositories.PostRepository;
import com.example.BlogBackend.Repositories.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> createPost(CreatePostDto createPostDto, String email) {
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
    public ResponseEntity<?> getPosts(String email, List<UUID> tags, String authorName, PostSorting sortOrder,
                                      Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                      Integer page, Integer size) {
        UserDto user = userService.loadUserByUsername(email);

        List<PostDto> posts = getPostsByUser(user, tags, authorName, sortOrder, minReadingTime,
                maxReadingTime, onlyMyCommunities, page, size);

        return ResponseEntity.ok(posts);
    }

    @Transactional
    public ResponseEntity<?> getPostsForUnauthorizedUser(List<UUID> tags, String authorName, PostSorting sortOrder,
                                                         Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                                         Integer page, Integer size) {
        List<PostDto> posts = getPostsByUser(null, tags, authorName, sortOrder, minReadingTime,
                maxReadingTime, onlyMyCommunities, page, size);

        return ResponseEntity.ok(posts);
    }

    private List<PostDto> getPostsByUser(UserDto user, List<UUID> tags, String authorName, PostSorting sortOrder,
                                         Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities, Integer page, Integer size) {
        List<PostFullDto> fullPosts;
        if (page <= 0 || size < 0){
            throw new IllegalArgumentException();
        }

        Pageable pageElements = PageRequest.of(page-1, size);

        if (tags != null && !tags.isEmpty()){
            fullPosts = postRepository.findPostsByParametersWithTags(authorName, minReadingTime, maxReadingTime, tags, pageElements);
        }
        else{
            fullPosts = postRepository.findPostsByParametersWithoutTags(authorName, minReadingTime, maxReadingTime, pageElements);
        }

        sortPosts(fullPosts, sortOrder);

        return checkLikes(fullPosts, user);
    }

    private List<PostDto> checkLikes(List<PostFullDto> fullPosts, UserDto user) {
        List<PostDto> posts = new ArrayList<>();

        for (PostFullDto post : fullPosts) {
            setLike(user, post);
            posts.add(PostMapper.postFullDtoToPostDto(post));
        }

        return posts;
    }

    private void setLike(UserDto user, PostFullDto post) {
        post.setHasLike(false);

        if (user != null && post.getLikesByUsers().contains(user)) {
            post.setHasLike(true);
        }
    }


    @Transactional
    public ResponseEntity<?> addLikeToPost(String email, UUID postId) {
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null) {
            throw new EntityNotFoundException();
        }


        UserDto user = userService.loadUserByUsername(email);
        if (post.getLikesByUsers().contains(user)) {
            throw new IllegalStateException();
        }

        post.getLikesByUsers().add(user);
        post.updateLikes();

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteLikeFromPost(String email, UUID postId) {
        PostFullDto post = postRepository.findPostFullDtoById(postId);
        if (post == null) {
            throw new EntityNotFoundException();
        }

        UserDto user = userService.loadUserByUsername(email);
        if (!post.getLikesByUsers().contains(user)) {
            throw new IllegalStateException();
        }

        post.getLikesByUsers().remove(user);
        post.updateLikes();

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForAuthorized(String email, UUID postId) {
        UserDto user = userService.loadUserByUsername(email);
        return ResponseEntity.ok(getInfoAboutPost(user, postId));
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForUnauthorized(UUID postId) {
        return ResponseEntity.ok(getInfoAboutPost(null, postId));
    }

    public ConcretePostInfoDto getInfoAboutPost(UserDto user, UUID postId) {
        PostFullDto post = postRepository.findPostFullDtoById(postId);

        if (post == null) {
            throw new EntityNotFoundException();
        }

        setLike(user, post);
        return PostMapper.postFullDtoToConcretePostDto(post);
    }

    private void sortPosts(List<PostFullDto> posts, PostSorting sortOrder){

        switch (sortOrder) {
            case CreateDesc:
                posts.sort(this::timeComparatorDESC);
                break;
            case CreateAsc:
                posts.sort(this::timeComparatorASC);
                break;
            case LikeAsc:
                posts.sort(this::likeComparatorASC);
                break;
            case LikeDesc:
                posts.sort(this::likeComparatorDESC);
                break;
        }
    }

    private int likeComparatorASC(PostFullDto firstPost, PostFullDto secondPost) {
        return Integer.compare(firstPost.getLikes(), secondPost.getLikes());
    }

    private int likeComparatorDESC(PostFullDto firstPost, PostFullDto secondPost){
        return Integer.compare(secondPost.getLikes(), firstPost.getLikes());
    }

    private int timeComparatorASC(PostFullDto firstPost, PostFullDto secondPost){
        return firstPost.getCreateTime().compareTo(secondPost.getCreateTime());

    }

    private int timeComparatorDESC(PostFullDto firstPost, PostFullDto secondPost){
        return secondPost.getCreateTime().compareTo(firstPost.getCreateTime());
    }
}
