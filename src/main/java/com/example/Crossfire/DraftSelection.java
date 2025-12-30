package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DraftSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Keep ONLY this one as @Id

    @ManyToOne
    @JoinColumn(name = "user_entry_id")
    private UserEntry userEntry;

    @ManyToOne
    @JoinColumn(name = "contestant_id")
    private Contestant contestant;

    @ManyToOne
    @JoinColumn(name = "contest_setup_id")
    private ContestSetup contestSetup;
}