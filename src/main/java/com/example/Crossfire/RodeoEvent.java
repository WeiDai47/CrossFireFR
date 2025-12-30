package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class RodeoEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;      // e.g., "National Finals Rodeo"
    private LocalDate eventDate;

    // Game Rules
    private int maxContestants;    // How many athletes a user drafts for their team (e.g., 6)
    private double salaryCap;      // Total budget for a user's draft (e.g., 50000.00)

    // Financials
    private BigDecimal entryFee;   // Cost for a user to enter this contest
    private BigDecimal prizePool;  // Total money to be split among winners

    // Logistics
    private String eventStatus;    // UPCOMING, LIVE, or COMPLETED
    private int roundNumber;       // e.g., 1, 2, or 0 for Average
    private String location;
    private boolean isLocked;      // True = No more drafting allowed

    // Relationships

    // 1. Payouts: Links to 1st, 2nd, 3rd place prize amounts
    @OneToMany(mappedBy = "rodeoEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PayoutStructure> payouts;

    // 2. Event Configurations: Links to how many spots are available per discipline
    // (e.g., 20 Team Roping spots, 10 Bull Riding spots)
    @OneToMany(mappedBy = "rodeoEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContestSetup> contestSetups;
}