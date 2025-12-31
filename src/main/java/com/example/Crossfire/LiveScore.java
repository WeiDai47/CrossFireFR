package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "live_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"rodeo_event_id", "contestant_id"})
})
@Data
public class LiveScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rodeo_event_id") // Added explicit join column names
    private RodeoEvent rodeoEvent;

    @ManyToOne
    @JoinColumn(name = "contestant_id")
    private Contestant contestant;

    private double score;
    private BigDecimal winnings;
    private boolean isOfficial;
}