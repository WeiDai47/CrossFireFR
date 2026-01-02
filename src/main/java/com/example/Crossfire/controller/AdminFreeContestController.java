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
@RequestMapping("/admin/contest/create/free")
public class AdminFreeContestController {

    @Autowired private RodeoEventRepository eventRepo;
    @Autowired private FantasyContestRepository contestRepo;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        return "admin-create-free";
    }

    @PostMapping
    public String process(@RequestParam Long rodeoEventId, @RequestParam String contestName,
                          @RequestParam(required = false) List<String> itemPrizes,
                          @RequestParam double salaryCap, @RequestParam int maxEntries,
                          @RequestParam("maxTotal") int maxTotalEntries) {

        FantasyContest contest = new FantasyContest();
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(BigDecimal.ZERO); // Always Free
        contest.setContestType(FantasyContest.ContestType.GUARANTEED); // Uses GTD logic for prizes

        if (itemPrizes != null) {
            contest.setItemPrizeNames(itemPrizes.stream().filter(s -> !s.isBlank()).toList());
        }

        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntries);
        contest.setMaxTotalEntries(maxTotalEntries);

        FantasyContest saved = contestRepo.save(contest);
        return "redirect:/admin/contest/" + saved.getId() + "/setup";
    }
}