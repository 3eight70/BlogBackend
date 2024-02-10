package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.UserMapper;
import com.example.BlogBackend.Models.Author.AuthorDto;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Repositories.PostRepository;
import com.example.BlogBackend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> getAuthorsList() {
        List<User> users = userRepository.findAll();
        List<AuthorDto> authors = new ArrayList<>();

        for (User user : users) {
            List<FullPost> posts = postRepository.findFullPostsByAuthorId(user.getId());

            if (posts != null && !posts.isEmpty()) {
                int totalLikes = posts.stream().mapToInt(FullPost::getLikes).sum();

                authors.add(UserMapper.userToAuthorDto(user, totalLikes, posts.size()));
            }
        }

        return ResponseEntity.ok(authors);
    }
}
