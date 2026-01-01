package com.example.Crossfire.controller;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminManagementController {

    // These MUST match the names you use in the methods below
    @Autowired private ContestantRepository athleteRepo;
    @Autowired private ContestSetupRepo setupRepo;
    @Autowired
    private RodeoEventRepository eventRepo;
    @Autowired
    private UserRepository userRepo; // This refers to the UserRepository.java file

    @Autowired
    private FantasyContestRepository contestRepo;

    @GetMapping("/contest/{id}/setup")
    public String showSetupRoster(@PathVariable Long id, Model model) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();
        model.addAttribute("contest", contest);
        model.addAttribute("allAthletes", athleteRepo.findAll()); // All cowboys in the system
        return "admin-setup-roster";
    }
    @PostMapping("/contest/setup/save")
    public String saveSetup(@RequestParam Long contestId,
                            @RequestParam String categoryName,
                            @RequestParam int slotsAvailable,
                            @RequestParam int maxPickPerUser,
                            @RequestParam List<Long> athleteIds) {

        FantasyContest contest = contestRepo.findById(contestId).orElseThrow();

        ContestSetup setup = new ContestSetup();
        setup.setFantasyContest(contest);
        setup.setCategoryName(categoryName);
        setup.setSlotsAvailable(slotsAvailable);
        setup.setMaxPickPerUser(maxPickPerUser);

        // Convert the IDs from the checkboxes into Athlete objects
        List<Contestant> eligible = athleteRepo.findAllById(athleteIds);
        setup.setEligibleContestants(eligible);

        setupRepo.save(setup);

        return "redirect:/admin/contest/" + contestId + "/setup";
    }
    @GetMapping("/event/create")
    public String showCreateEvent() {
        return "admin-create-event";
    }

    @PostMapping("/event/create")
    public String processCreateEvent(@ModelAttribute RodeoEvent event) {
        eventRepo.save(event);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/contest/create")
    public String showCreateContest(Model model) {
        // Now 'eventRepo' is recognized and will fetch existing rodeos for the dropdown
        model.addAttribute("events", eventRepo.findAll());
        // Updated to your new filename
        return "admin-create-fantasycontest";
    }

    @PostMapping("/contest/create")
    public String processCreateContest(@RequestParam Long rodeoEventId,
                                       @RequestParam String contestName,
                                       @RequestParam BigDecimal entryFee,
                                       @RequestParam BigDecimal prizePool,
                                       @RequestParam double salaryCap,
                                       @RequestParam int maxEntriesPerUser) {

        FantasyContest contest = new FantasyContest();
        // Uses the ID from the dropdown to link the Rodeo to the Contest
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(entryFee);
        contest.setPrizePool(prizePool);
        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntriesPerUser);

        FantasyContest savedContest = contestRepo.save(contest);
        return "redirect:/admin/contest/" + savedContest.getId() + "/setup";
    }
    // 1. Show the management list
    @GetMapping("/manage")
    public String manageContent(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        model.addAttribute("contests", contestRepo.findAll());
        return "admin-manage-list";
    }

    // 2. Delete a Rodeo Event
    @Transactional
    @GetMapping("/contest/delete/{id}")
    public String deleteContest(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean refund) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();

        if (refund) {
            for (UserEntry entry : contest.getUserEntries()) {
                User user = entry.getUser();
                if (user != null && contest.getEntryFee() != null) {
                    user.setBalance(user.getBalance().add(contest.getEntryFee()));
                    userRepo.save(user);
                }
            }
        }

        contestRepo.delete(contest);
        return "redirect:/admin/manage";
    }

    @Transactional
    @GetMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean refund) {
        RodeoEvent event = eventRepo.findById(id).orElseThrow();

        if (refund) {
            // Loop through all contests in this rodeo to refund them
            for (FantasyContest contest : event.getContests()) {
                for (UserEntry entry : contest.getUserEntries()) {
                    User user = entry.getUser();
                    if (user != null) {
                        user.setBalance(user.getBalance().add(contest.getEntryFee()));
                        userRepo.save(user);
                    }
                }
            }
        }

        eventRepo.delete(event);
        return "redirect:/admin/manage";
    }
    // This method handles clicking the link /admin/add
    @GetMapping("/admin/add")
    public String showAddContestantForm(Model model) {
        // This MUST match your filename: admin-add.html
        model.addAttribute("contestant", new Contestant());
        return "admin-add";
    }

    // This method handles the actual "Save" button click
    @PostMapping("/admin/add")
    public String saveContestant(@ModelAttribute Contestant contestant) {
        athleteRepo.save(contestant);
        return "redirect:/admin/manage";
    }

}