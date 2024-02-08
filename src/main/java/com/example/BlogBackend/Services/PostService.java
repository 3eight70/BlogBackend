package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.CommentMapper;
import com.example.BlogBackend.Models.Comment.Comment;
import com.example.BlogBackend.Models.Comment.CommentDto;
import com.example.BlogBackend.Models.Community.Community;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Pagination;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Mappers.PostMapper;
import com.example.BlogBackend.Models.Post.*;
import com.example.BlogBackend.Models.Tag.Tag;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Repositories.CommunityRepository;
import com.example.BlogBackend.Repositories.PostRepository;
import com.example.BlogBackend.Repositories.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public ResponseEntity<?> createPost(CreatePostDto createPostDto, User user, Community community) {
        FullPost post = new FullPost();
        List<Tag> tags = tagRepository.findAllById(createPostDto.getTags());

        if (tags.size() != createPostDto.getTags().size()) {
            throw new EntityNotFoundException();
        }

        post.setAddressId(createPostDto.getAddressId());
        post.setAuthor(user.getFullName());
        post.setAuthorId(user.getId());
        post.setTitle(createPostDto.getTitle());
        post.setDescription(createPostDto.getDescription());
        post.setReadingTime(createPostDto.getReadingTime());
        post.setImage(createPostDto.getImage());
        post.setTags(tags);

        if (community != null){
            post.setCommunityId(community.getId());
            post.setCommunityName(community.getName());
        }
        postRepository.save(post);

        return ResponseEntity.ok(post.getId());
    }

    @Transactional
    public ResponseEntity<?> getPosts(User user, List<UUID> tags, String authorName, PostSorting sortOrder,
                                      Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                      Integer page, Integer size) {

        List<PostDto> posts = getPostsByUser(user, tags, authorName, sortOrder, minReadingTime,
                maxReadingTime, onlyMyCommunities);

        return sendResponse(posts, page, size);
    }

    @Transactional
    public ResponseEntity<?> getPostsForUnauthorizedUser(List<UUID> tags, String authorName, PostSorting sortOrder,
                                                         Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities,
                                                         Integer page, Integer size) {
        if (onlyMyCommunities){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Пользователь не авторизован"), HttpStatus.BAD_REQUEST);
        }

        List<PostDto> posts = getPostsByUser(null, tags, authorName, sortOrder, minReadingTime,
                maxReadingTime, onlyMyCommunities);

        return sendResponse(posts, page, size);
    }

    public ResponseEntity<?> sendResponse(List<PostDto> posts, int page, int size){
        Map<String, Object> response = new HashMap<>();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, posts.size());

        response.put("posts", posts.subList(startIndex, endIndex));
        response.put("pagination", new Pagination(size, page, getPagination(posts.size(), size)));
        return ResponseEntity.ok(response);
    }

    private int getPagination(int postsSize, int size){
        int paginationSize = (int)Math.ceil((postsSize)/(double)size);

        if (postsSize % size == 0){
            paginationSize+=1;
        }

        return paginationSize;
    }

    private List<PostDto> getPostsByUser(User user, List<UUID> tags, String authorName, PostSorting sortOrder,
                                         Integer minReadingTime, Integer maxReadingTime, Boolean onlyMyCommunities) {
        List<FullPost> posts = postRepository.findAll();

        List<FullPost> filteredPosts;

        if (!onlyMyCommunities){
            filteredPosts = posts.stream()
                    .filter(p -> authorName == null || p.getAuthor().contains(authorName))
                    .filter(p -> minReadingTime == null || p.getReadingTime() >= minReadingTime)
                    .filter(p -> maxReadingTime == null || p.getReadingTime() <= maxReadingTime)
                    .filter(p -> p.getCommunityId() == null || !isCommunityClosed(p.getCommunityId())
                            || userInClosedCommunity(p.getCommunityId(), user))
                    .filter(p-> tags == null || checkTags(p.getTags(), tags))
                    .sorted((post1, post2) -> {
                        Comparator<FullPost> comparator = getComparator(sortOrder);
                        return comparator.compare(post1, post2);
                    })
                    .collect(Collectors.toList());
        }
        else{
            filteredPosts = posts.stream()
                    .filter(p -> authorName == null || p.getAuthor().contains(authorName))
                    .filter(p -> minReadingTime == null || p.getReadingTime() >= minReadingTime)
                    .filter(p -> maxReadingTime == null || p.getReadingTime() <= maxReadingTime)
                    .filter(p -> user == null || userInCommunity(p.getCommunityId(), user))
                    .filter(p-> tags == null || checkTags(p.getTags(), tags))
                    .sorted((post1, post2) -> {
                        Comparator<FullPost> comparator = getComparator(sortOrder);
                        return comparator.compare(post1, post2);
                    })
                    .collect(Collectors.toList());
        }

        return checkLikes(filteredPosts, user);
    }

    public Comparator getComparator(PostSorting sortOrder){
        switch (sortOrder) {
            case CreateDesc:
                return Comparator.comparing(FullPost::getCreateTime).reversed();
            case CreateAsc:
                return Comparator.comparing(FullPost::getCreateTime);
            case LikeAsc:
                return Comparator.comparingInt(FullPost::getLikes);
            case LikeDesc:
                return Comparator.comparingInt(FullPost::getLikes).reversed();
            default:
                return Comparator.comparing(FullPost::getCreateTime);
        }
    }
    public boolean checkTags(List<Tag> tags, List<UUID> requestTags){
        for (UUID tagId : requestTags){
            if (tags.contains(tagRepository.findTagById(tagId))){
                return true;
            }
        }
        return false;
    }

    private boolean isCommunityClosed(UUID communityId){
        Community community = communityRepository.findCommunityById(communityId);
        if (community == null){
            return false;
        }
        return community.getIsClosed();
    }

    private boolean userInCommunity(UUID communityId, User user){
        if (communityId == null){
            return false;
        }
        Community community = communityRepository.findCommunityById(communityId);

        if (community.getSubscribers().contains(user) || community.getAdministrators().contains(user)){
            return true;
        }

        return false;
    }
    private boolean userInClosedCommunity(UUID communityId, User user){
        if (!isCommunityClosed(communityId)){
            return true;
        }

        return userInCommunity(communityId, user);
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

        List<CommentDto> comments = new ArrayList<>();
        for (Comment comment: post.getComments()){
            comments.add(CommentMapper.commentToCommentDto(comment));
        }

        setLike(user, post);
        return PostMapper.postFullDtoToConcretePostDto(post, comments);
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
