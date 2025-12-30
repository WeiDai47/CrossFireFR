package com.example.Crossfire.contoller;

import com.example.Crossfire.Contestant;
import com.example.Crossfire.repository.ContestantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // Use @Controller (not @RestController) for HTML pages
public class AdminController {

    @Autowired
    private ContestantRepository repository;

    // Show the form
    @GetMapping("/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("contestant", new Contestant());
        return "admin-add"; // This looks for admin-add.html
    }

    // Process the form data
    @PostMapping("/admin/add")
    public String saveContestant(@ModelAttribute Contestant contestant) {
        repository.save(contestant);
        return "redirect:/"; // Go back to home page after saving
    }
}