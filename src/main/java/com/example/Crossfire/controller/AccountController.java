package com.example.Crossfire.controller;

import com.example.Crossfire.User;
import com.example.Crossfire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Add this
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/deposit")
    public String showDepositPage(@RequestParam String username, Model model) {
        userRepo.findByUsername(username).ifPresent(user -> model.addAttribute("user", user));
        return "deposit"; // Ensure this is outside the ifPresent block
    }

    @PostMapping("/add-funds")
    public String addFunds(@RequestParam String username, @RequestParam BigDecimal amount) {
        userRepo.findByUsername(username).ifPresent(user -> {
            user.addWinnings(amount);
            userRepo.save(user);
        });
        return "redirect:/?username=" + username;
    }
}