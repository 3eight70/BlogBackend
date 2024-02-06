package com.example.BlogBackend.Models.Community;

import com.example.BlogBackend.Models.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Data
@Entity
@Table(name="communities")
@AllArgsConstructor
@NoArgsConstructor
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина названия группы - 1")
    private String name;

    private String description;

    @Column(nullable = false)
    private Boolean isClosed = false;

    @Column(nullable = false)
    private int subscribersCount = 0;

    @Column(nullable = false)
    private int administratorsCount = 0;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "community_administrators",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "administrator_id")
    )
    private List<User> administrators = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "community_subscribers",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private List<User> subscribers = new ArrayList<>();

    @PrePersist
    private void init(){
        createTime = LocalDateTime.now();
    }

    public void updateSubscribers() {
        this.subscribersCount = this.subscribers.size();
    }

    public void updateAdministrators() {
        this.administratorsCount = this.administrators.size();
    }
}
