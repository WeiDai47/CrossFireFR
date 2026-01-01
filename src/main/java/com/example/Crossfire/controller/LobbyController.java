package com.example.Crossfire.controller;

import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.User;
import com.example.Crossfire.UserEntry;
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.UserEntryRepository;
import com.example.Crossfire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LobbyController {

    @Autowired
    private FantasyContestRepository contestRepo;

    @Autowired
    private UserRepository userRepo; // Needed to find the user by their name

    @Autowired
    private UserEntryRepository userEntryRepo;

    /**
     * This handles the home page (the Lobby).
     * It fetches all available contests and identifies the logged-in user.
     */
    @GetMapping("/")
    public String showLobby(@RequestParam(required = false) String username, Model model) {
        // 1. Fetch all contests from the database for the grid display
        List<FantasyContest> contests = contestRepo.findAll();
        model.addAttribute("contests", contests);

        // 2. Check if a username was provided in the URL (e.g., /?username=RodeoKing)
        if (username != null && !username.isEmpty()) {
            userRepo.findByUsername(username).ifPresent(user -> {
                model.addAttribute("user", user);

                // Fetch the user's entries to see which contests they joined
                List<UserEntry> userEntries = userEntryRepo.findByUsername(username);

                // Create a list of Contest IDs the user is already in
                List<Long> enteredContestIds = userEntries.stream()
                        .map(entry -> entry.getFantasyContest().getId())
                        .collect(Collectors.toList());

                model.addAttribute("enteredContestIds", enteredContestIds);
            });
        }

        return "lobby";

    }
}