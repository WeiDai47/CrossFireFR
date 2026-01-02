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

    @Autowired private ContestantRepository athleteRepo;
    @Autowired private ContestSetupRepo setupRepo;
    @Autowired private RodeoEventRepository eventRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private LiveScoreRepository liveScoreRepo;
    @Autowired private FantasyContestRepository contestRepo;


    // --- CONTESTANT MANAGEMENT ---
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("contestant", new Contestant());
        return "admin-add";
    }

    @PostMapping("/add")
    public String saveContestantaddpage(@ModelAttribute Contestant contestant) {
        athleteRepo.save(contestant);
        return "redirect:/admin/manage";
    }

    @GetMapping("/admin/add")
    public String showAddContestantForm(Model model) {
        model.addAttribute("contestant", new Contestant());
        return "admin-add";
    }

    @PostMapping("/admin/add")
    public String saveContestant(@ModelAttribute Contestant contestant) {
        athleteRepo.save(contestant);
        return "redirect:/admin/manage";
    }

    // --- DASHBOARD & MANAGEMENT ---
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/manage")
    public String manageContent(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        model.addAttribute("contests", contestRepo.findAll());
        return "admin-manage-list";
    }

    // --- EVENT CREATION ---
    @GetMapping("/event/create")
    public String showCreateEvent() {
        return "admin-create-event";
    }

    @PostMapping("/event/create")
    public String processCreateEvent(@ModelAttribute RodeoEvent event) {
        eventRepo.save(event);
        return "redirect:/admin/dashboard";
    }

    // --- CONTEST CREATION (Merged with Guaranteed/Dynamic Logic) ---
    @GetMapping("/contest/create")
    public String showCreateContestForm(Model model) {
        model.addAttribute("events", eventRepo.findAll());
        return "admin-create-fantasycontest";
    }

    @PostMapping("/contest/create")
    public String processCreateContest(@RequestParam Long rodeoEventId,
                                       @RequestParam String contestName,
                                       @RequestParam BigDecimal entryFee,
                                       @RequestParam(required = false) BigDecimal prizePool,
                                       @RequestParam String contestType,
                                       @RequestParam double prizeCurveSteepness,
                                       @RequestParam double salaryCap,
                                       @RequestParam int maxEntriesPerUser) {

        FantasyContest contest = new FantasyContest();
        contest.setRodeoEvent(eventRepo.findById(rodeoEventId).orElseThrow());
        contest.setContestName(contestName);
        contest.setEntryFee(entryFee);

        // Logic for Guaranteed vs Dynamic
        contest.setContestType(FantasyContest.ContestType.valueOf(contestType));
        if (prizePool != null) {
            contest.setPrizePool(prizePool); // This is your "Guaranteed" floor
        }

        contest.setPrizeCurveSteepness(prizeCurveSteepness);
        contest.setSalaryCap(salaryCap);
        contest.setMaxEntriesPerUser(maxEntriesPerUser);
        contest.setHouseTakePercentage(0.10);

        FantasyContest savedContest = contestRepo.save(contest);
        return "redirect:/admin/contest/" + savedContest.getId() + "/setup";
    }

    // --- ROSTER SETUP ---
    @GetMapping("/contest/{id}/setup")
    public String showSetupRoster(@PathVariable Long id, Model model) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();
        model.addAttribute("contest", contest);
        model.addAttribute("allAthletes", athleteRepo.findAll());
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

        List<Contestant> eligible = athleteRepo.findAllById(athleteIds);
        setup.setEligibleContestants(eligible);

        setupRepo.save(setup);
        return "redirect:/admin/contest/" + contestId + "/setup";
    }

    // --- DELETION & REFUNDS ---
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

    // --- FINALIZATION & PAYOUTS ---
    @Transactional
    @PostMapping("/contest/{id}/finalize")
    public String finalizeAndPay(@PathVariable Long id) {
        FantasyContest contest = contestRepo.findById(id).orElseThrow();
        List<LiveScore> results = liveScoreRepo.findByRodeoEvent(contest.getRodeoEvent());
        List<UserEntry> entries = contest.getUserEntries();

        for (UserEntry entry : entries) {
            entry.setLiveScore(entry.calculateTotalScore(results));
        }

        entries.sort((a, b) -> Double.compare(b.getLiveScore(), a.getLiveScore()));
        List<BigDecimal> prizeList = contest.calculateScalingPrizes();

        for (int i = 0; i < prizeList.size(); i++) {
            if (i < entries.size()) {
                User winner = entries.get(i).getUser();
                winner.addWinnings(prizeList.get(i));
                userRepo.save(winner);
            }
        }
        return "redirect:/admin/manage?msg=payout_complete";
    }
}