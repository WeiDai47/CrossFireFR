package com.example.Crossfire.controller;

import com.example.Crossfire.Contestant;
import com.example.Crossfire.LiveScore;
import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.repository.LiveScoreRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import com.example.Crossfire.repository.ContestantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // Use @Controller to return HTML views
@RequestMapping("/admin/scoring")
public class ScoreUpdateController {

    @Autowired
    private RodeoEventRepository eventRepo;

    @Autowired
    private ContestantRepository athleteRepo;

    @Autowired
    private LiveScoreRepository scoreRepo;

    // This fixes the 404/Error page when you go to http://localhost:8080/admin/scoring
    @GetMapping
    public String showScoringPage(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        model.addAttribute("athletes", athleteRepo.findAll());
        return "admin-scoring-live"; // Must match the filename admin-scoring-live.html
    }

    // This handles the JavaScript Fetch request from the page
    @PostMapping("/update")
    @ResponseBody // This allows returning a string message instead of a new HTML page
    public String updateScore(@RequestParam Long eventId,
                              @RequestParam Long contestantId,
                              @RequestParam double score) {

        RodeoEvent event = eventRepo.findById(eventId).orElseThrow();
        Contestant contestant = athleteRepo.findById(contestantId).orElseThrow();

        // Check if score entry exists, otherwise create it
        LiveScore liveScore = scoreRepo.findByRodeoEventAndContestant(event, contestant)
                .orElse(new LiveScore());

        liveScore.setRodeoEvent(event);
        liveScore.setContestant(contestant);
        liveScore.setScore(score);

        scoreRepo.save(liveScore);

        return "Score of " + score + " saved for " + contestant.getName();
    }
}