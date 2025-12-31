package com.example.Crossfire.repository;

import com.example.Crossfire.LiveScore;
import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.Contestant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // Don't forget this import

public interface LiveScoreRepository extends JpaRepository<LiveScore, Long> {

    List<LiveScore> findByRodeoEvent(RodeoEvent rodeoEvent);

    // This is the method the AdminController is looking for:
    Optional<LiveScore> findByRodeoEventAndContestant(RodeoEvent event, Contestant contestant);
}