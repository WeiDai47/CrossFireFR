package com.example.Crossfire.repository;

import com.example.Crossfire.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA handles all basic CRUD operations
}