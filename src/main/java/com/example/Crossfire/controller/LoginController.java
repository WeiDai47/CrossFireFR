package com.example.Crossfire.controller;

import com.example.Crossfire.User;
import com.example.Crossfire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verify password
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                return "redirect:/?username=" + user.getUsername();
            } else {
                // Password doesn't match
                return "redirect:/login?error=InvalidCredentials";
            }
        }

        // User not found
        return "redirect:/login?error=UserNotFound";
    }

}