package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "fantasy_contests")
public class FantasyContest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // This makes the column name "fantasy_contest_id" in SQL
    @Column(name = "fantasy_contest_id")
    private Long id;

    private String contestName;

    // The new column to limit how many times one user can enter this contest
    @Column(name = "max_entries_per_user")
    private int maxEntriesPerUser = 1; // Default to 1 entry per user

    // This property name must match the 'mappedBy' in RodeoEvent
    @ManyToOne
    @JoinColumn(name = "rodeo_event_id")
    private RodeoEvent rodeoEvent;

    private double salaryCap;
    private BigDecimal entryFee;
    private BigDecimal prizePool;
    // Add to FantasyContest.java
    private double prizeCurveSteepness = 1.5; // Default value (1.5 is standard)
    private double houseTakePercentage = 0.10; // Your 10% fee

    // This property name must match the 'mappedBy' in ContestSetup
    @OneToMany(mappedBy = "fantasyContest", cascade = CascadeType.ALL)
    private List<ContestSetup> contestSetups;

    // Inside FantasyContest.java

    @OneToMany(mappedBy = "fantasyContest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntry> userEntries;




    @ElementCollection
    @CollectionTable(name = "contest_payout_structure", joinColumns = @JoinColumn(name = "contest_id"))
    @Column(name = "percentage")
    @OrderColumn
    private List<Double> payoutPercentages; // Index 0 is 1st place, Index 1 is 2nd, etc.

    // Inside FantasyContest.java

    public BigDecimal calculateTotalCollected() {
        if (entryFee == null || userEntries == null) return BigDecimal.ZERO;
        return entryFee.multiply(BigDecimal.valueOf(userEntries.size()));
    }

    public BigDecimal calculateNetPrizePool() {
        BigDecimal total = calculateTotalCollected();
        BigDecimal houseCut = total.multiply(BigDecimal.valueOf(houseTakePercentage));
        return total.subtract(houseCut);
    }

    /**
     * Returns a list of specific dollar amounts for each place (1st, 2nd, 3rd...)
     * based on the CURRENT number of entries.
     */
    public List<BigDecimal> getCalculatedPrizeValues() {
        BigDecimal netPool = calculateNetPrizePool();
        return payoutPercentages.stream()
                .map(pct -> netPool.multiply(BigDecimal.valueOf(pct)))
                .toList();
    }
    // Add these to FantasyContest.java

    public List<BigDecimal> calculateScalingPrizes() {
        int totalEntries = (userEntries != null) ? userEntries.size() : 0;
        if (totalEntries == 0) return new ArrayList<>();

        // 1. Determine spots to pay (e.g., top 25% of the field)
        int spotsToPay = (int) Math.max(1, Math.ceil(totalEntries * 0.25));

        // 2. Calculate the Net Prize Pool (Total Fees - 10% House)
        BigDecimal totalCollected = entryFee.multiply(BigDecimal.valueOf(totalEntries));
        BigDecimal netPool = totalCollected.multiply(BigDecimal.valueOf(1.0 - houseTakePercentage));

        List<BigDecimal> prizes = new ArrayList<>();

        // 3. Power Curve Math
        // p = 1.5 is a standard "top heavy" curve.
        // Higher p = more to 1st place. Lower p = flatter payouts.
        double p = 1.5;
        double totalWeight = 0;
        for (int i = 1; i <= spotsToPay; i++) {
            totalWeight += 1.0 / Math.pow(i, p);
        }

        // 4. Assign Dollars based on weights
        for (int i = 1; i <= spotsToPay; i++) {
            double weight = 1.0 / Math.pow(i, p);
            double share = weight / totalWeight;

            BigDecimal prize = netPool.multiply(BigDecimal.valueOf(share))
                    .setScale(2, RoundingMode.HALF_UP);
            prizes.add(prize);
        }

        return prizes;
    }
    // Inside FantasyContest.java

    public enum ContestType {
        GUARANTEED, DYNAMIC
    }

    private ContestType contestType = ContestType.DYNAMIC; // Default to dynamic

    /**
     * The "Smart" prize pool display logic.
     */
    public BigDecimal getDisplayPrizePool() {
        // 1. Calculate the dynamic pool based on current entries
        int actualEntries = (userEntries != null) ? userEntries.size() : 0;
        BigDecimal totalCollected = entryFee.multiply(BigDecimal.valueOf(actualEntries));
        BigDecimal dynamicAmount = totalCollected.multiply(BigDecimal.valueOf(1.0 - houseTakePercentage));

        // 2. Logic check: If Guaranteed, show the higher of the two. If Dynamic, just show the scaling amount.
        if (this.contestType == ContestType.GUARANTEED && this.prizePool != null) {
            // Returns the higher of the Guaranteed amount or the Dynamic amount
            return this.prizePool.max(dynamicAmount);
        }

        return dynamicAmount;
    }
}