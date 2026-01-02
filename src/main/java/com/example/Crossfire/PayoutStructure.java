package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PayoutStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rank;             // e.g., 1 for 1st place, 2 for 2nd
    private BigDecimal amount;    // The dollar amount for this rank

    @ManyToOne
    @JoinColumn(name = "rodeo_event_id")
    private RodeoEvent rodeoEvent; // Links this payout to a specific Rodeo Event


    public List<BigDecimal> calculateCurrentPrizes(FantasyContest contest) {
        // 1. Get total money collected
        int entryCount = contest.getUserEntries().size(); //
        BigDecimal totalCollected = contest.getEntryFee().multiply(BigDecimal.valueOf(entryCount)); //

        // 2. Subtract the 10% House Take
        BigDecimal houseCut = totalCollected.multiply(BigDecimal.valueOf(contest.getHouseTakePercentage()));
        BigDecimal netPrizePool = totalCollected.subtract(houseCut);

        // 3. Calculate individual tier prizes
        List<BigDecimal> calculatedPrizes = new ArrayList<>();
        for (Double percentage : contest.getPayoutPercentages()) {
            calculatedPrizes.add(netPrizePool.multiply(BigDecimal.valueOf(percentage)));
        }

        return calculatedPrizes;
    }
}