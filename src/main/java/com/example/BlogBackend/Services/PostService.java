package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Community.Community;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Mappers.PostMapper;
import com.example.BlogBackend.Models.Post.*;
import com.example.BlogBackend.Models.Tag.Tag;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Repositories.PostRepository;
import com.example.BlogBackend.Repositories.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Transactional
    public ResponseEntity<?> createPost(CreatePostDto createPostDto, User user, Community community) {
        FullPost post = new FullPost();
        List<Tag> tags = tagRepository.findAllById(createPostDto.getTags());

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
        if (community != null){
            post.setCommunityId(community.getId());
            post.setCommunityName(community.getName());
        }
        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getPosts(User user, List<UUID> tags, String authorName, PostSorting sortOrder,
                                      Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                      Integer page, Integer size) {

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

    private List<PostDto> getPostsByUser(User user, List<UUID> tags, String authorName, PostSorting sortOrder,
                                         Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities, Integer page, Integer size) {
        List<FullPost> fullPosts;
        if (page <= 0 || size < 0){
            throw new IllegalArgumentException();
        }

        Pageable pageElements = PageRequest.of(page-1, size, getSorting(sortOrder));

        if (tags != null && !tags.isEmpty()){
            if (user != null) {
                fullPosts = postRepository.findPostsByParametersWithTagsIfUserNotNull(authorName, minReadingTime, maxReadingTime, tags, user.getId(), pageElements);
            }
            else{
                fullPosts = postRepository.findPostsByParametersWithTagsIfUserNull(authorName, minReadingTime, maxReadingTime, tags, pageElements);
            }
        }
        else{
            if (user != null) {
                fullPosts = postRepository.findPostsByParametersWithoutTagsIfUserNotNull(authorName, minReadingTime, maxReadingTime, user.getId(), pageElements);
            }
            else{
                fullPosts = postRepository.findPostsByParametersWithoutTagsIfUserNull(authorName, minReadingTime, maxReadingTime, pageElements);
            }
        }

        return checkLikes(fullPosts, user);
    }

    public List<PostDto> checkLikes(List<FullPost> fullPosts, User user) {
        List<PostDto> posts = new ArrayList<>();

        for (FullPost post : fullPosts) {
            setLike(user, post);
            posts.add(PostMapper.postFullDtoToPostDto(post));
        }

        return posts;
    }

    private void setLike(User user, FullPost post) {
        post.setHasLike(false);

        if (user != null && post.getLikesByUsers().contains(user)) {
            post.setHasLike(true);
        }
    }


    @Transactional
    public ResponseEntity<?> addLikeToPost(User user, UUID postId) {
        FullPost post = postRepository.findPostFullDtoById(postId);
        if (post == null) {
            throw new EntityNotFoundException();
        }

        if (post.getLikesByUsers().contains(user)) {
            throw new IllegalStateException();
        }

        post.getLikesByUsers().add(user);
        post.updateLikes();

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteLikeFromPost(User user, UUID postId) {
        FullPost post = postRepository.findPostFullDtoById(postId);
        if (post == null) {
            throw new EntityNotFoundException();
        }

        if (!post.getLikesByUsers().contains(user)) {
            throw new IllegalStateException();
        }

        post.getLikesByUsers().remove(user);
        post.updateLikes();

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForAuthorized(User user, UUID postId) {
        return ResponseEntity.ok(getInfoAboutPost(user, postId));
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutConcretePostForUnauthorized(UUID postId) {
        return ResponseEntity.ok(getInfoAboutPost(null, postId));
    }

    public ConcretePostInfoDto getInfoAboutPost(User user, UUID postId) {
        FullPost post = postRepository.findPostFullDtoById(postId);

        if (post == null) {
            throw new EntityNotFoundException();
        }

        setLike(user, post);
        return PostMapper.postFullDtoToConcretePostDto(post);
    }

    public Sort getSorting(PostSorting sortOrder){

        switch (sortOrder) {
            case CreateDesc:
                return Sort.by(Sort.Direction.DESC, "create_time");
            case CreateAsc:
                return Sort.by(Sort.Direction.ASC, "create_time");
            case LikeAsc:
                return Sort.by(Sort.Direction.ASC, "likes");
            case LikeDesc:
                return Sort.by(Sort.Direction.DESC, "likes");
            default:
                return Sort.by(Sort.Direction.ASC, "create_time");
        }
    }

}
