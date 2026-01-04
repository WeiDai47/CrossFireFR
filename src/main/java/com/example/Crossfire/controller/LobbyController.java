package com.example.Crossfire.controller;

import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.User;
import com.example.Crossfire.UserEntry;
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.UserEntryRepository;
import com.example.Crossfire.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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
    public String showLobby(@RequestParam(required = false) String username, Model model, HttpSession session) {
        // 1. Fetch all contests
        List<FantasyContest> contests = contestRepo.findAll();
        model.addAttribute("contests", contests);

        // 2. Determine who the user is (Priority: Session, then RequestParam)
        User currentUser = (User) session.getAttribute("loggedInUser");
        String effectiveUsername = (currentUser != null) ? currentUser.getUsername() : username;

        // 3. If we have a username (from either source), load the data
        if (effectiveUsername != null && !effectiveUsername.isEmpty()) {
            userRepo.findByUsername(effectiveUsername).ifPresent(user -> {
                model.addAttribute("user", user);

                // Fetch entries using the effective username
                List<UserEntry> userEntries = userEntryRepo.findByUsername(effectiveUsername);
                List<Long> enteredContestIds = userEntries.stream()
                        .map(entry -> entry.getFantasyContest().getId())
                        .collect(Collectors.toList());

                model.addAttribute("enteredContestIds", enteredContestIds);
            });
        }

        return "lobby";

    }
}