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
            return "redirect:/draft/" + contestId + "?error=" + result;
        }

        return "redirect:/draft/my-teams?username=" + username;
    }

    @GetMapping("/my-teams")
    public String showMyTeams(@RequestParam(required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            List<UserEntry> myEntries = userEntryRepo.findByUsername(username);
            model.addAttribute("entries", myEntries);
            model.addAttribute("searchedUsername", username);
        }
        return "my-teams";
    }
}