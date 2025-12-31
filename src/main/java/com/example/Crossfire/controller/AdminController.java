package com.example.Crossfire.controller;

import com.example.Crossfire.Contestant;
import com.example.Crossfire.LiveScore;
import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.repository.ContestantRepository;
import com.example.Crossfire.repository.LiveScoreRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin") // All routes now start with /admin
public class AdminController {

    @Autowired
    private ContestantRepository contestantRepo;

    @Autowired
    private RodeoEventRepository eventRepo;

    @Autowired
    private LiveScoreRepository liveScoreRepo;

    // --- EXISTING CONTESTANT MANAGEMENT ---

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("contestant", new Contestant());
        return "admin-add";
    }

    @PostMapping("/add")
    public String saveContestant(@ModelAttribute Contestant contestant) {
        contestantRepo.save(contestant);
        return "redirect:/";
    }

    // --- NEW LIVE SCORING MANAGEMENT ---

    // 1. Show page to select a Rodeo to score
    @GetMapping("/scoring")
    public String selectEvent(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        return "admin-select-event";
    }

    // 2. Show the scoreboard for the chosen rodeo
    @GetMapping("/scoring/{eventId}")
    public String scoreBoard(@PathVariable Long eventId, Model model) {
        RodeoEvent event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // We get all contestants. Later, you can filter this by the specific rodeo roster.
        List<Contestant> athletes = contestantRepo.findAll();

        model.addAttribute("event", event);
        model.addAttribute("athletes", athletes);
        return "admin-event-scoreboard";
    }

    // 3. Process the score update
    @PostMapping("/scoring/update")
    public String updateScore(@RequestParam Long eventId,
                              @RequestParam Long contestantId,
                              @RequestParam double score) {
        RodeoEvent event = eventRepo.findById(eventId).orElseThrow();
        Contestant athlete = contestantRepo.findById(contestantId).orElseThrow();

        // Check for existing score to update it, or create a new one
        LiveScore liveScore = liveScoreRepo.findByRodeoEventAndContestant(event, athlete)
                .orElse(new LiveScore());

        liveScore.setRodeoEvent(event);
        liveScore.setContestant(athlete);
        liveScore.setScore(score);
        liveScore.setOfficial(true); // Mark as official for the leaderboard

        liveScoreRepo.save(liveScore);

        return "redirect:/admin/scoring/" + eventId;
    }
    // Add this to your AdminController.java
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}