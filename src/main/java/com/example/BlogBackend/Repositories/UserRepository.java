package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.User.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDto, Long> {
    UserDto findByEmail(String email);
}
