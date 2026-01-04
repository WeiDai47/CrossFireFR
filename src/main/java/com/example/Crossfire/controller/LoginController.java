package com.example.Crossfire.controller;

import com.example.Crossfire.User;
import com.example.Crossfire.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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
                               HttpSession session,
                               Model model) {

        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
// 1. Verify password first
            if (user.getPassword() != null && user.getPassword().equals(password)) {

                // 2. SAVE USER TO SESSION (Important for your new Interceptor!)
                session.setAttribute("loggedInUser", user);

                // 3. Check Role AFTER successful password check
                if ("ADMIN".equals(user.getRole())) {
                    return "redirect:/admin/dashboard";
                }

                // Default for regular users
                return "redirect:/?username=" + user.getUsername();
            }
            else {
                // Password doesn't match
                return "redirect:/login?error=InvalidCredentials";
            }
        }

        // User not found
        return "redirect:/login?error=UserNotFound";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // This clears all session data including "loggedInUser"
        return "redirect:/login?logout=true";
    }

}