package com.example.Crossfire.controller;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/payouts")
public class AdminPayoutController {

    @Autowired private FantasyContestRepository contestRepo;
    @Autowired private LiveScoreRepository liveScoreRepo;
    @Autowired private UserRepository userRepo;

    /**
     * Shows all contests that are finished but haven't been paid yet.
     */
    @GetMapping
    public String showPayoutDashboard(Model model) {
        List<FantasyContest> pendingContests = contestRepo.findAll().stream()
                .filter(c -> !c.isPaid())
                .toList();
        model.addAttribute("contests", pendingContests);
        return "admin-payout-dashboard";
    }

    /**
     * Shows a ranked leaderboard and the exact dollar amount each user will receive.
     */
    @GetMapping("/preview/{id}")
    public String previewPayout(@PathVariable Long id, Model model) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();
        List<LiveScore> results = liveScoreRepo.findByRodeoEvent(contest.getRodeoEvent());

        List<UserEntry> entries = contest.getUserEntries();
        for (UserEntry entry : entries) {
            entry.setLiveScore(entry.calculateTotalScore(results));
        }

        // Sort descending by score
        entries.sort((a, b) -> Double.compare(b.getLiveScore(), a.getLiveScore()));

        model.addAttribute("contest", contest);
        model.addAttribute("entries", entries);
        model.addAttribute("prizes", contest.calculateScalingPrizes());
        return "admin-payout-preview";
    }

    /**
     * Performs the actual balance updates for the winners.
     */
    @Transactional
    @PostMapping("/process/{id}")
    public String processManualPayout(@PathVariable Long id) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();

        if (contest.isPaid()) {
            return "redirect:/admin/payouts?error=already_paid";
        }

        List<LiveScore> results = liveScoreRepo.findByRodeoEvent(contest.getRodeoEvent());
        List<UserEntry> entries = contest.getUserEntries();

        // Recalculate and Rank
        for (UserEntry entry : entries) {
            entry.setLiveScore(entry.calculateTotalScore(results));
        }
        entries.sort((a, b) -> Double.compare(b.getLiveScore(), a.getLiveScore()));

        List<BigDecimal> prizeList = contest.calculateScalingPrizes();

        // Transfer funds to winners
        for (int i = 0; i < prizeList.size(); i++) {
            if (i < entries.size()) {
                User winner = entries.get(i).getUser();
                winner.addWinnings(prizeList.get(i));
                userRepo.save(winner);
            }
        }

        // Finalize the contest
        contest.setPaid(true);
        contestRepo.save(contest);

        return "redirect:/admin/payouts?success=true";
    }
}