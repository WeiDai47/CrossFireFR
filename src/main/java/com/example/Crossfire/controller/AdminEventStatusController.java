package com.example.Crossfire.controller;

import com.example.Crossfire.RodeoEvent;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/event-status")
public class AdminEventStatusController {

    @Autowired
    private RodeoEventRepository eventRepo;

    @GetMapping
    public String showStatusManagement(Model model) {
        List<RodeoEvent> events = eventRepo.findAll();
        model.addAttribute("events", events);
        return "admin-event-status";
    }

    @PostMapping("/update")
    public String updateEventStatus(@RequestParam Long eventId, @RequestParam String status) {
        RodeoEvent event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventId));

        event.setEventStatus(status);
        eventRepo.save(event);

        return "redirect:/admin/event-status?success=true";
    }
}