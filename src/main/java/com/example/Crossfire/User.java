package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "users") // 'user' is a reserved keyword in some SQL dialects
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // Will hold encoded hash later
    private String displayName;

    // One user can have many entries across different rodeo events
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEntry> entries;
}