package com.example.Crossfire.controller;

import com.example.Crossfire.User;
import com.example.Crossfire.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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
    public String showDepositPage(HttpSession session, Model model) {
        // Use session instead of @RequestParam for better security/stability
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Refresh user data from DB to get latest balance
        User refreshedUser = userRepo.findById(user.getId()).orElseThrow();
        model.addAttribute("user", refreshedUser);
        return "deposit";
    }

    @PostMapping("/add-funds")
    public String addFunds(@RequestParam BigDecimal amount, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            User dbUser = userRepo.findById(user.getId()).orElseThrow();
            dbUser.addWinnings(amount);
            userRepo.save(dbUser);
            // Update session object too
            session.setAttribute("loggedInUser", dbUser);
        }
        return "redirect:/";
    }
}