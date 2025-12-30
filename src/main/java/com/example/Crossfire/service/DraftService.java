package com.example.Crossfire.service;

import com.example.Crossfire.*;
import com.example.Crossfire.repository.UserEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DraftService {

    @Autowired
    private UserEntryRepository userEntryRepo;

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

        // 2. Check "Max Picks Per Category"
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

        // 3. Finalize and Save
        entry.setTotalTeamSalary(totalCost);
        userEntryRepo.save(entry);

        return "Success! Your team is entered.";
    }
}