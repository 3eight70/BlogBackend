package com.example.BlogBackend.Repositories;

import com.example.BlogBackend.Models.User.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserDto, UUID> {
    UserDto findByEmail(String email);
}
