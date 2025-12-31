package com.example.Crossfire.service;

import com.example.Crossfire.Contestant;
import com.example.Crossfire.EventResult;
import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.repository.EventResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {
    @Autowired
    private EventResultRepository resultRepo;

    @Transactional
    public void postScore(RodeoEvent event, Contestant athlete, double score) {
        // Find existing result or create new one
        EventResult result = resultRepo.findByRodeoEventAndContestant(event, athlete)
                .orElse(new EventResult());

        result.setRodeoEvent(event);
        result.setContestant(athlete);
        result.setScore(score);

        resultRepo.save(result);
    }
}