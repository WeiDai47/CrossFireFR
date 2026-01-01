package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
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

    // This property name must match the 'mappedBy' in ContestSetup
    @OneToMany(mappedBy = "fantasyContest", cascade = CascadeType.ALL)
    private List<ContestSetup> contestSetups;

    // Inside FantasyContest.java

    @OneToMany(mappedBy = "fantasyContest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntry> userEntries;
}