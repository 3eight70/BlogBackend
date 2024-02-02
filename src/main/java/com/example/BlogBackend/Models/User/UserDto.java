package com.example.BlogBackend.Models.User;

import com.example.BlogBackend.Models.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    private String fullName;

    private Date birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;


    @Column(unique = true, nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$", message = "Invalid phone number")
    private String phoneNumber;

    @Column(length = 1000, nullable = false)
    @Pattern(regexp = "^(?=.*\\d).{6,}$", message = "Password must be at least 6 letters and have at least 1 digit")
    private String password;

    @PrePersist
    private void init(){
        createTime = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
