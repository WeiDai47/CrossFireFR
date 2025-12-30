package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // CHANGE: Point to the Contest, not the Rodeo directly
    // This allows one Rodeo to have many different contests (High Stakes, Free, etc.)
    @ManyToOne
    @JoinColumn(name = "fantasy_contest_id")
    private FantasyContest fantasyContest;

    @OneToMany(mappedBy = "userEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DraftSelection> selections = new ArrayList<>();

    private double totalTeamSalary;
}