package com.example.Crossfire.repository;

import com.example.Crossfire.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA handles all basic CRUD operations

    // Add this line to fix the error in AccountController
    Optional<User> findByUsername(String username);
}