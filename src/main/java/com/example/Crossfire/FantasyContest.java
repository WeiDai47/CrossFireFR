package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class FantasyContest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contestName;

    // This property name must match the 'mappedBy' in RodeoEvent
    @ManyToOne
    @JoinColumn(name = "rodeo_event_id")
    private RodeoEvent rodeoEvent;

    private double salaryCap;
    private BigDecimal entryFee;
    private BigDecimal prizePool;

    // This property name must match the 'mappedBy' in ContestSetup
    @OneToMany(mappedBy = "fantasyContest", cascade = CascadeType.ALL)
    private List<ContestSetup> contestSetups;

    @OneToMany(mappedBy = "fantasyContest")
    private List<UserEntry> userEntries;
}