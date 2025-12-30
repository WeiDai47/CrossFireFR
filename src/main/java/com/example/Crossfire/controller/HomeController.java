package com.example.Crossfire.controller;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

//    @Autowired
//    private RodeoEventRepository eventRepo;
//
//    @GetMapping("/")
//    public String showLobby(Model model) {
//        // Send all upcoming rodeos to the page
//        model.addAttribute("events", eventRepo.findAll());
//        return "lobby"; // This looks for lobby.html
//    }
}