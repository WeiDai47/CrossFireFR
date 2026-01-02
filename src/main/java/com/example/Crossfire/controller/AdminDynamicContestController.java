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
@RequestMapping("/admin/contest/create/dynamic")
public class AdminDynamicContestController {

    @Autowired private RodeoEventRepository eventRepo;
    @Autowired private FantasyContestRepository contestRepo;

    @GetMapping
    public String showForm(@RequestParam(defaultValue = "false") boolean isFree, Model model) {
        model.addAttribute("events", eventRepo.findAll());
        model.addAttribute("isFree", isFree);
        return "admin-create-dynamic"; // We will create this template
    }

    @PostMapping
    public String process(@RequestParam Long rodeoEventId, @RequestParam String contestName,
                          @RequestParam BigDecimal entryFee, @RequestParam double curve,
                          @RequestParam double salaryCap, @RequestParam int maxEntries) {

        FantasyContest contest = new FantasyContest();
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(entryFee);
        contest.setContestType(FantasyContest.ContestType.DYNAMIC);
        contest.setPrizeCurveSteepness(curve);
        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntries);
        contest.setHouseTakePercentage(0.10);

        FantasyContest saved = contestRepo.save(contest);
        return "redirect:/admin/contest/" + saved.getId() + "/setup";
    }
}