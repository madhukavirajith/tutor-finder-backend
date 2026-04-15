package com.madhuka.tutor_finder_backend.entity; // Adjust package name to yours

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Tells Spring: "This class represents a Table in MySQL"
@Table(name = "users") // The table name will be 'users'
@Data // Lombok magic: Auto-creates Getters, Setters, toString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Will be encrypted later

    private String role; // "ADMIN", "TUTOR", "PARENT"

    private boolean enabled = true;
}