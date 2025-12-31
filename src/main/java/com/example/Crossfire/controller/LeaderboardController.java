package com.example.Crossfire.controller;
import org.springframework.transaction.annotation.Transactional;
import com.example.Crossfire.LiveScore;
import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.UserEntry;
import com.example.Crossfire.repository.LiveScoreRepository; // Using this now
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.UserEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LeaderboardController {

    @Autowired
    private FantasyContestRepository contestRepo;

    @Autowired
    private LiveScoreRepository liveScoreRepo; // Changed from resultRepo

    @Autowired
    private UserEntryRepository userEntryRepository;

    @GetMapping("/contest/{contestId}/leaderboard")
    public String showLeaderboard(@PathVariable Long contestId, Model model) {
        FantasyContest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));

        // FIX 1: Use the autowired 'liveScoreRepo' instance, not the Interface name
        List<LiveScore> results = liveScoreRepo.findByRodeoEvent(contest.getRodeoEvent());

        // FIX 2: map to the scores using LiveScore::getScore
        Map<Long, Double> scoreMap = results.stream()
                .collect(Collectors.toMap(
                        r -> r.getContestant().getId(),
                        LiveScore::getScore,
                        (existing, replacement) -> existing
                ));

        List<UserEntry> entries = contest.getUserEntries();

        for (UserEntry entry : entries) {
            double total = entry.getSelections().stream()
                    .mapToDouble(selection -> scoreMap.getOrDefault(selection.getContestant().getId(), 0.0))
                    .sum();
            entry.setLiveScore(total);
        }

        entries.sort((a, b) -> {
            int scoreCompare = Double.compare(b.getLiveScore(), a.getLiveScore());
            return (scoreCompare != 0) ? scoreCompare : a.getUsername().compareToIgnoreCase(b.getUsername());
        });

        model.addAttribute("contest", contest);
        model.addAttribute("rodeoName", contest.getRodeoEvent().getEventName());
        model.addAttribute("entries", entries);

        return "leaderboard";
    }

    @GetMapping("/my-teams")
    @Transactional(readOnly = true)
    public String showMyTeams(@RequestParam String username, Model model) {
        // 1. PRINT TO CONSOLE IMMEDIATELY
        System.out.println("--- DEBUG START ---");
        System.out.println("Browser requested My Teams for: " + username);

        List<UserEntry> entries = userEntryRepository.findByUsername(username);

        // 2. PRINT THE COUNT
        System.out.println("Database found " + entries.size() + " entries.");

        for (UserEntry entry : entries) {
            FantasyContest contest = entry.getFantasyContest();
            List<LiveScore> eventScores = liveScoreRepo.findByRodeoEvent(contest.getRodeoEvent());

            double myScore = entry.calculateTotalScore(eventScores);
            entry.setLiveScore(myScore);

            List<UserEntry> allContestEntries = contest.getUserEntries();
            long higherTeams = allContestEntries.stream()
                    .map(otherEntry -> otherEntry.calculateTotalScore(eventScores))
                    .filter(score -> score > myScore)
                    .count();

            entry.setRank((int) higherTeams + 1);

            // 3. PRINT INDIVIDUAL RESULTS
            System.out.println("Calculated Rank for Team " + entry.getId() + ": #" + entry.getRank());
        }

        System.out.println("--- DEBUG END ---");

        model.addAttribute("entries", entries);
        model.addAttribute("searchedUsername", username);
        return "my-teams";
    }

}