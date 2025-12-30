package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class LiveScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RodeoEvent rodeoEvent; // Which rodeo?

    @ManyToOne
    private Contestant contestant; // Which athlete?

    private double score;           // e.g., 88.5 points for a ride
    private BigDecimal winnings;    // The actual $ amount they won in the round
    private boolean isOfficial;     // Set to true once the judges finalize the score
}