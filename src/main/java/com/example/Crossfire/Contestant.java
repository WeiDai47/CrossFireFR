package com.example.Crossfire;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Contestant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT") // Allows for a long info section
    private String bio;

    private double salary; // e.g., 9500.00

    private String eventType; // Bull Riding, Steer Wrestling, etc.

    private boolean isInjured = false;

    @ManyToMany(mappedBy = "eligibleContestants")
    private List<ContestSetup> eligibleSetups;

}
