package com.example.Crossfire.repository;

import com.example.Crossfire.FantasyContest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FantasyContestRepository extends JpaRepository<FantasyContest, Long> {

    // This allows you to list all contests (High Stakes, Free, etc.)
    // for a specific real-world Rodeo.
    List<FantasyContest> findByRodeoEventId(Long rodeoEventId);
}