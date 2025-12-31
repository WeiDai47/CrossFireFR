package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "event_results",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rodeo_event_id", "contestant_id"})
        }
)
@Data
public class EventResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rodeo_event_id", nullable = false)
    private RodeoEvent rodeoEvent;

    @ManyToOne
    @JoinColumn(name = "contestant_id", nullable = false)
    private Contestant contestant;

    private double score;

    // Optional: Add a field for the specific round if a rodeo has multiple rounds
    private Integer roundNumber;
}