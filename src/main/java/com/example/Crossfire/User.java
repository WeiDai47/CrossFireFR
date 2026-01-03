package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Add this field

    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
    private String displayName;


    // --- FUNDS SECTION ---
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    // --- RELATIONSHIPS ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntry> entries = new ArrayList<>(); // Initialized to avoid null errors

    /**
     * Helper method to add winnings to the user's account balance.
     */
    public void addWinnings(BigDecimal amount) {
        if (amount != null) {
            this.balance = this.balance.add(amount);
        }
    }

}



