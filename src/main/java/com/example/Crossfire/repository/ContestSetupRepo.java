package com.example.Crossfire.repository;

import com.example.Crossfire.ContestSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestSetupRepo extends JpaRepository<ContestSetup, Long> {
}