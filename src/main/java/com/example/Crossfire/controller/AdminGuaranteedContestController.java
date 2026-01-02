package com.example.Crossfire.controller;

import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/contest/create/guaranteed")
public class AdminGuaranteedContestController {

    @Autowired private RodeoEventRepository eventRepo;
    @Autowired private FantasyContestRepository contestRepo;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        return "admin-create-guaranteed";
    }

    @PostMapping
    public String process(@RequestParam Long rodeoEventId, @RequestParam String contestName,
                          @RequestParam BigDecimal entryFee, @RequestParam BigDecimal prizePool,
                          @RequestParam double curve, @RequestParam double salaryCap,
                          @RequestParam int maxEntries) {

        FantasyContest contest = new FantasyContest();
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(entryFee);
        contest.setPrizePool(prizePool);
        contest.setContestType(FantasyContest.ContestType.GUARANTEED);
        contest.setPrizeCurveSteepness(curve);
        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntries);
        contest.setHouseTakePercentage(0.10);

        FantasyContest saved = contestRepo.save(contest);
        return "redirect:/admin/contest/" + saved.getId() + "/setup";
    }
}