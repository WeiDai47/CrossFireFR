package com.example.Crossfire.controller;

import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.repository.FantasyContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LobbyController {

    @Autowired
    private FantasyContestRepository contestRepo;

    /**
     * This handles the home page (the Lobby).
     * It fetches all the "Games" (Contests) available for users to join.
     */
    @GetMapping("/")
    public String showLobby(Model model) {
        // 1. Fetch all contests from the database
        List<FantasyContest> contests = contestRepo.findAll();

        // 2. Add them to the model so the HTML can loop through them
        model.addAttribute("contests", contests);

        // 3. Return the lobby.html template
        return "lobby";
    }
}