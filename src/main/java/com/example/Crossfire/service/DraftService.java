package com.example.Crossfire.service;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.UserEntryRepository;
import com.example.Crossfire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DraftService {

    @Autowired
    private UserEntryRepository userEntryRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public String validateAndSaveEntry(UserEntry entry) {
        // Now getting the game rules from the FantasyContest instead of the Rodeo
        FantasyContest contest = entry.getFantasyContest();



        // 1. Check Salary Cap (Uses the contest's specific cap)
        double totalCost = entry.getSelections().stream()
                .mapToDouble(selection -> selection.getContestant().getSalary())
                .sum();

        if (totalCost > contest.getSalaryCap()) {
            return "Error: Team is over the $" + String.format("%.0f", contest.getSalaryCap()) + " salary cap for this contest!";
        }

        // 2. Check User Balance
        User user = userRepo.findByUsername(entry.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal fee = contest.getEntryFee();
        if (user.getBalance().compareTo(fee) < 0) {
            return "Error: Insufficient funds. This contest costs $" + fee;
        }

        // 3. Check "Max Picks Per Category"
        // This ensures the user picked exactly what the admin required for this specific game
        for (ContestSetup setup : contest.getContestSetups()) {
            long countInThisCategory = entry.getSelections().stream()
                    .filter(s -> s.getContestSetup().getId().equals(setup.getId()))
                    .count();

            if (countInThisCategory != setup.getMaxPickPerUser()) {
                return "Error: You must pick exactly " + setup.getMaxPickPerUser() +
                        " for " + setup.getCategoryName();
            }
        }

        // 4. Deduct Funds and Save
        user.setBalance(user.getBalance().subtract(fee));
        userRepo.save(user);

        entry.setTotalTeamSalary(totalCost);
        userEntryRepo.save(entry);

        return "Success! Your team is entered.";
    }
}