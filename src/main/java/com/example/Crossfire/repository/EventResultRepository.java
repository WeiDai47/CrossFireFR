package com.example.Crossfire.repository;

import com.example.Crossfire.EventResult;
import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.Contestant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventResultRepository extends JpaRepository<EventResult, Long> {

    // Find all scores for a specific Rodeo (used for the leaderboard)
    List<EventResult> findByRodeoEvent(RodeoEvent rodeoEvent);

    // Find a specific athlete's score for a specific Rodeo
    Optional<EventResult> findByRodeoEventAndContestant(RodeoEvent rodeoEvent, Contestant contestant);
}