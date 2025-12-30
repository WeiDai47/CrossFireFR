package com.example.Crossfire.repository;

import com.example.Crossfire.LiveScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LiveScoreRepository extends JpaRepository<LiveScore, Long> {
    // This helper method lets you find scores for a specific rodeo event
    List<LiveScore> findByRodeoEventId(Long eventId);
}