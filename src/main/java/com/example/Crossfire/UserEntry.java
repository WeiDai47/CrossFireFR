package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's specific entry (team) into a Fantasy Contest.
 */
@Entity
@Data
public class UserEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Links this entry to a specific Contest.
     * This allows multiple contests (e.g., "Pro League" vs "Casual")
     * to exist for the same Rodeo Event.
     */
    @ManyToOne
    @JoinColumn(name = "fantasy_contest_id")
    private FantasyContest fantasyContest;

    /**
     * The list of athletes drafted for this specific entry.
     */
    @OneToMany(mappedBy = "userEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DraftSelection> selections = new ArrayList<>();

    private double totalTeamSalary;

    /**
     * Holds the calculated score for display in the UI.
     * @Transient ensures this is NOT saved in the database,
     * as scores change live and should be calculated on the fly.
     */
    @Transient
    private double liveScore;
    @Transient
    private int rank; // Add this next to your liveScore field
    /**
     * Calculates the total score for this team based on a provided list
     * of results for a specific Rodeo Event.
     * * @param results The list of EventResults for the relevant rodeo.
     * @return The sum of scores for all drafted contestants.
     */
    public double calculateTotalScore(List<LiveScore> results) {
        double total = 0;
        for (DraftSelection selection : selections) {
            Long athleteId = selection.getContestant().getId();

            // Search the results for the matching athlete
            total += results.stream()
                    .filter(r -> r.getContestant().getId().equals(athleteId))
                    .mapToDouble(LiveScore::getScore)
                    .findFirst()
                    .orElse(0.0);
        }
        return total;
    }
    // Add this to UserEntry.java
    public double getTotalScore() {
        return this.liveScore;
    }
    /**
     * Helper method to add a selection and maintain the bidirectional relationship.
     */
    public void addSelection(DraftSelection selection) {
        selections.add(selection);
        selection.setUserEntry(this);
    }
}