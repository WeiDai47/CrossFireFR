package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class RodeoEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private String location;
    private LocalDate eventDate;
    private String eventStatus;
    private double salaryCap;

    @OneToMany(mappedBy = "rodeoEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiveScore> liveScores;

    // Also ensure your relationship to FantasyContest has it too!
    @OneToMany(mappedBy = "rodeoEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FantasyContest> contests;
}