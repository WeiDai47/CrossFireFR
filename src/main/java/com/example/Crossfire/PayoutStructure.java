package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

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
}