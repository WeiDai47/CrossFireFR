package com.example.Crossfire.controller;

import com.example.Crossfire.Contestant;
import com.example.Crossfire.repository.ContestantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminRosterController {

    @Autowired
    private ContestantRepository athleteRepo;

    @GetMapping("/roster")
    public String showMasterRoster(@RequestParam(required = false) String search, Model model) {
        List<Contestant> athletes;
        if (search != null && !search.isEmpty()) {
            athletes = athleteRepo.findByNameContainingIgnoreCase(search);
        } else {
            athletes = athleteRepo.findAll();
        }
        model.addAttribute("athletes", athletes);
        model.addAttribute("search", search);
        model.addAttribute("newAthlete", new Contestant()); // For the bottom "Add" row
        return "admin-master-roster";
    }

    // NEW: Handle Single-Row Update (Ajax style for speed)
    @PostMapping("/roster/update")
    @ResponseBody
    public String updateAthlete(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String eventType,
                                @RequestParam double salary,
                                @RequestParam String bio,
                                @RequestParam boolean isInjured) {
        Contestant c = athleteRepo.findById(id).orElseThrow();
        c.setName(name);
        c.setEventType(eventType);
        c.setSalary(salary);
        c.setBio(bio);
        c.setInjured(isInjured);
        athleteRepo.save(c);
        return "Saved";
    }

    @PostMapping("/roster/add")
    public String saveContestant(@ModelAttribute Contestant contestant) {
        athleteRepo.save(contestant);
        return "redirect:/admin/roster";
    }

    // Optional: Delete mapping to keep the roster clean
    @GetMapping("/roster/delete/{id}")
    public String deleteContestant(@PathVariable Long id) {
        athleteRepo.deleteById(id);
        return "redirect:/admin/roster";
    }
}
