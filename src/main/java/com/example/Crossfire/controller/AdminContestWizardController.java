package com.example.Crossfire.controller;

import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/contest/create")
public class AdminContestWizardController {

    @Autowired
    private RodeoEventRepository eventRepo;

    @GetMapping("/select")
    public String showSelectionPage() {
        return "admin-contest-type-select";
    }

    // This helper provides common data like events to the specialized controllers
    public void prepareBaseModel(Model model) {
        model.addAttribute("events", eventRepo.findAll());
    }
}