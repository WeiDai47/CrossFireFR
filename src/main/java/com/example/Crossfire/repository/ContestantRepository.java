package com.example.Crossfire.repository;

import com.example.Crossfire.Contestant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestantRepository extends JpaRepository<Contestant, Long> {
    List<Contestant> findByNameContainingIgnoreCase(String name);
}