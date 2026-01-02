package com.example.Crossfire.controller;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.*;
import com.example.Crossfire.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/draft")
public class DraftController {

    @Autowired private ContestantRepository contestantRepo;
    @Autowired private FantasyContestRepository contestRepo; // Changed from EventRepo
    @Autowired private ContestSetupRepo setupRepo;
    @Autowired private DraftService draftService;
    @Autowired private UserEntryRepository userEntryRepo;

    // The ID in the URL is now the FantasyContest ID
    @GetMapping("/{contestId}")
    public String showDraftPage(@PathVariable Long contestId,
                                @RequestParam String username,
                                @RequestParam(required = false) String error,
                                Model model) {

        FantasyContest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        model.addAttribute("contest", contest);
        model.addAttribute("event", contest.getRodeoEvent()); // Still pass event for display info
        model.addAttribute("setups", contest.getContestSetups());

        if (error != null) {
            model.addAttribute("errorMessage", error);
        }

        return "draft-team";
    }

    @PostMapping("/{contestId}/submit")
    public String submitDraft(@PathVariable Long contestId,
                              @RequestParam List<String> contestantPicks,
                              @RequestParam String username) {

        FantasyContest contest = contestRepo.findById(contestId).orElseThrow();

        // 1. THIS IS THE "USAGE" - Check the entry limit before doing anything else
        long entryCount = userEntryRepo.countByUsernameAndFantasyContestId(username, contestId);

        if (entryCount >= contest.getMaxEntriesPerUser()) {
            String errorMsg = "Error: You have already reached the limit of " +
                    contest.getMaxEntriesPerUser() + " entries for this contest.";
            return "redirect:/draft/" + contestId + "?error=" + errorMsg;
        }

        UserEntry entry = new UserEntry();
        entry.setUsername(username);
        entry.setFantasyContest(contest); // Link to the specific game

        List<DraftSelection> selections = new ArrayList<>();
        for (String pick : contestantPicks) {
            String[] ids = pick.split("-");
            Long setupId = Long.parseLong(ids[0]);
            Long athleteId = Long.parseLong(ids[1]);

            DraftSelection selection = new DraftSelection();
            selection.setUserEntry(entry);
            selection.setContestant(contestantRepo.findById(athleteId).orElseThrow());
            selection.setContestSetup(setupRepo.findById(setupId).orElseThrow());
            selections.add(selection);
        }

        entry.setSelections(selections);

        // Service now validates against FantasyContest rules (cap/slots)
        String result = draftService.validateAndSaveEntry(entry);

        if (result.startsWith("Error")) {
            // Updated to ensure username is passed back even on error if needed
            return "redirect:/draft/" + contestId + "?username=" + username + "&error=" + result;
        }

        return "redirect:/my-teams?username=" + username;
    }

    @GetMapping({"/my-teams"})
    public String showMyTeams(@RequestParam(required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            List<UserEntry> myEntries = userEntryRepo.findByUsername(username);
            model.addAttribute("entries", myEntries);
            model.addAttribute("searchedUsername", username);
        }
        return "my-teams";
    }
    @GetMapping("/contest/{contestId}/leaderboard")
    public String showLeaderboard(@PathVariable Long contestId, Model model) {
        FantasyContest contest = contestRepo.findById(contestId).orElseThrow();

        // Get all entries and sort them by their calculated total score (descending)
        List<UserEntry> entries = contest.getUserEntries();
        entries.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));

        model.addAttribute("contest", contest);
        model.addAttribute("entries", entries);
        return "leaderboard";
    }
}