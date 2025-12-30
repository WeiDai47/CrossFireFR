package com.example.Crossfire.contoller;

import com.example.Crossfire.repository.LiveScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/scores")
public class ScoreUpdateController {

    @Autowired
    private LiveScoreRepository scoreRepo;

    @PostMapping("/update")
    public String updateScore(@RequestParam Long eventId, @RequestParam Long contestantId, @RequestParam double score) {
        // Logic to find or create the LiveScore entry
        // Update the score and save to DB
        return "Score updated live for contestant!";
    }
}