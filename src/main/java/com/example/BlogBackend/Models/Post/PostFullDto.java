package com.example.BlogBackend.Models.Post;

import com.example.BlogBackend.Models.Comment.CommentDto;
import com.example.BlogBackend.Models.TagDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name="posts")
@AllArgsConstructor
@NoArgsConstructor
public class PostFullDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String description;

    @Column(nullable = false)
    private int readingTime;

    private String image;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String author;

    private UUID communityId;

    private String communityName;

    private UUID addressId;

    @Column(nullable = false)
    private int likes = 0;

    @Column(nullable = false)
    private boolean hasLike;

    @Column(nullable = false)
    private int commentsCount = 0;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id")
    private List<TagDto> tags = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private List<CommentDto> comments = new ArrayList<>();

    @PrePersist
    private void init(){
        createTime = LocalDateTime.now();
    }

}