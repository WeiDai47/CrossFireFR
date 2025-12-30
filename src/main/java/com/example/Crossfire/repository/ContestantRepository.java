package com.example.Crossfire.repository;

import com.example.Crossfire.Contestant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestantRepository extends JpaRepository<Contestant, Long> {
    // Spring Boot automatically creates all the code for Save, Update, Delete, and Find!
}