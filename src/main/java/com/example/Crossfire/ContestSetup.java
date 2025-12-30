package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class ContestSetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private int slotsAvailable;
    private int maxPickPerUser;

    // The 'mappedBy' in FantasyContest looks for this exact variable name
    @ManyToOne
    @JoinColumn(name = "fantasy_contest_id")
    private FantasyContest fantasyContest;

    @ManyToMany
    @JoinTable(
            name = "setup_eligible_contestants",
            joinColumns = @JoinColumn(name = "contest_setup_id"),
            inverseJoinColumns = @JoinColumn(name = "contestant_id")
    )
    private List<Contestant> eligibleContestants;
}