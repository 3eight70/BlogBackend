package com.example.BlogBackend.Models.Tag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="tags")
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private String name;
}
