package com.example.Crossfire.repository;

import com.example.Crossfire.RodeoEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RodeoEventRepository extends JpaRepository<RodeoEvent, Long> {
}