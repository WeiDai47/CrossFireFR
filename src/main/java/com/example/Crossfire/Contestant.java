package com.example.Crossfire;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

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

}
