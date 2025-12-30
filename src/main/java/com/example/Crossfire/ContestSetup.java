package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ContestSetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName; // e.g., "Bull Riding", "Team Roping"
    private int slotsAvailable;  // e.g., 10 for Bull Riding, 20 for Team Roping
    private int maxPickPerUser;  // How many a FANTASY user picks (e.g., 1 or 2)

    @ManyToOne
    @JoinColumn(name = "rodeo_event_id")
    private RodeoEvent rodeoEvent;
}