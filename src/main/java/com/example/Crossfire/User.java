package com.example.Crossfire;

import jakarta.persistence.*;
import lombok.Data;
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

    private String password;
    private String displayName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEntry> entries;
}