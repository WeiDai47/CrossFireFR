package com.example.Crossfire.controller;

import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public String process(@RequestParam Long rodeoEventId,
                          @RequestParam String contestName,
                          @RequestParam BigDecimal entryFee,
                          @RequestParam List<BigDecimal> fixedPrizes, // Captured from dynamic inputs
                          @RequestParam double salaryCap,
                          @RequestParam int maxEntries,
    @RequestParam int maxTotal) {


        FantasyContest contest = new FantasyContest();
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(entryFee);
        contest.setContestType(FantasyContest.ContestType.GUARANTEED);

        // Filter out any empty/zero inputs from the admin
        List<BigDecimal> cleanPrizes = fixedPrizes.stream()
                .filter(p -> p != null && p.compareTo(BigDecimal.ZERO) > 0)
                .toList();
        contest.setFixedPrizeAmounts(cleanPrizes);

        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntries);
        contest.setHouseTakePercentage(0.10);

        FantasyContest saved = contestRepo.save(contest);
        return "redirect:/admin/contest/" + saved.getId() + "/setup";
    }
    }
