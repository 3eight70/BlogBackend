package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.CommunityMapper;
import com.example.BlogBackend.Mappers.UserMapper;
import com.example.BlogBackend.Models.Community.*;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.CreatePostDto;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Models.Post.PostDto;
import com.example.BlogBackend.Models.Post.PostSorting;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Models.User.UserDto;
import com.example.BlogBackend.Repositories.CommunityRepository;
import com.example.BlogBackend.Repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final PostService postService;
    private final PostRepository postRepository;

    @Transactional
    public ResponseEntity<?> getCommunities() {
        List<Community> communities = communityRepository.findAll();
        List<CommunityDto> communitiesDto = new ArrayList<>();

        for (Community community : communities) {
            communitiesDto.add(CommunityMapper.communityToCommunityDto(community));
        }
        return ResponseEntity.ok(communitiesDto);
    }

    @Transactional
    public ResponseEntity<?> createCommunity(CreateCommunityDto createCommunityDto, User user) {
        if (communityRepository.findByName(createCommunityDto.getName()) != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Сообщество с таким названием уже существует"), HttpStatus.BAD_REQUEST);
        }

        Community community = CommunityMapper.createCommunityDtoToCommunity(createCommunityDto);

        community.getAdministrators().add(user);
        community.updateAdministrators();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> subscribe(User user, UUID id) {
        Community community = getCommunityById(id);

        if (community.getAdministrators().contains(user)) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь является администратором данного сообщества"), HttpStatus.BAD_REQUEST);
        }

        if (community.getSubscribers().contains(user)) {
            throw new IllegalStateException();
        }

        community.getSubscribers().add(user);
        community.updateSubscribers();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> unsubscribe(User user, UUID id) {
        Community community = getCommunityById(id);

        if (!community.getSubscribers().contains(user)) {
            throw new IllegalStateException();
        }

        community.getSubscribers().remove(user);
        community.updateSubscribers();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getUsersCommunities(User user) {
        List<Community> communities = communityRepository.findAll();
        List<CommunityUserDto> userRoles = new ArrayList<>();

        for (Community community : communities) {
            if (community.getSubscribers().contains(user)) {
                userRoles.add(CommunityMapper.communityToCommunityUserDto(community, user, CommunityRole.Subscriber));
            } else if (community.getAdministrators().contains(user)) {
                userRoles.add(CommunityMapper.communityToCommunityUserDto(community, user, CommunityRole.Administrator));
            }
        }

        return ResponseEntity.ok(userRoles);
    }

    @Transactional
    public ResponseEntity<?> getInfoAboutCommunity(UUID id) {
        Community community = getCommunityById(id);
        List<UserDto> administrators = new ArrayList<>();

        for (User administrator : community.getAdministrators()) {
            administrators.add(UserMapper.userToUserDto(administrator));
        }

        return ResponseEntity.ok(CommunityMapper.communityToCommunityFullDto(community, administrators));
    }

    private Community getCommunityById(UUID id) {
        Community community = communityRepository.findCommunityById(id);
        if (community == null) {
            throw new EntityNotFoundException();
        }

        return community;
    }

    @Transactional
    public ResponseEntity<?> createPostInCommunity(CreatePostDto createPostDto, UUID id, User user) {
        Community community = getCommunityById(id);

        if (community == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                    "Заданного сообщества не существует"), HttpStatus.NOT_FOUND);
        }

        if (!community.getAdministrators().contains(user)) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
                    "Пользователь не является администратором"), HttpStatus.FORBIDDEN);
        }

        return postService.createPost(createPostDto, user, community);
    }

    @Transactional
    public ResponseEntity<?> getUsersGreatestRole(UUID id, User user) {
        Community community = getCommunityById(id);
        CommunityRole greatestRole = null;

        if (community.getAdministrators().contains(user)) {
            greatestRole = CommunityRole.Administrator;
        } else if (community.getSubscribers().contains(user)) {
            greatestRole = CommunityRole.Subscriber;
        }

        if (greatestRole != null) {
            return ResponseEntity.ok(greatestRole);
        }

        return ResponseEntity.ok("null");
    }

    @Transactional
    public ResponseEntity<?> getCommunityPosts(UUID id, User user, List<UUID> tags, int page, int size, PostSorting sortOrder) {
        Community community = getCommunityById(id);

        if (community.getIsClosed() && user == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(),
                    "Пользователь не авторизован"), HttpStatus.UNAUTHORIZED);
        } else if (community.getIsClosed() && !community.getAdministrators().contains(user) && !community.getSubscribers().contains(user)) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь не находится в данном закрытом сообществе"), HttpStatus.BAD_REQUEST);
        }

        List<FullPost> fullPosts = postRepository.findAllFullPostsInCommunity(id);
        List<FullPost> filteredPosts = fullPosts.stream()
                .filter(p -> tags == null || postService.checkTags(p.getTags(), tags))
                .sorted((post1, post2) -> {
                    Comparator<FullPost> comparator = postService.getComparator(sortOrder);
                    return comparator.compare(post1, post2);
                })
                .collect(Collectors.toList());

        List<PostDto> posts = postService.checkLikes(filteredPosts, user);

        return postService.sendResponse(posts, page, size);
    }
}
