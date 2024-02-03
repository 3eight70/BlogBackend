package com.example.BlogBackend.Models.Comment;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="tags")
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Column(nullable = false)
    private String content;

    private LocalDateTime modifiedDate;

    private LocalDateTime deleteDate;

    @Column(nullable = false)
    private UUID authorId;

    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int subComments;

    @PrePersist
    private void init(){
        createTime = LocalDateTime.now();
    }
}