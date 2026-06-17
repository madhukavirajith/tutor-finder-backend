package com.madhuka.tutor_finder_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; // "ADMIN", "TUTOR", "PARENT"

    // Personal info fields (mainly used for tutors, but available for all)
    private String title;      // Mr, Mrs, Miss, Dr, Prof
    private String firstName;
    private String lastName;
    private String gender;     // Male, Female, Other
    private String dateOfBirth;

    private boolean enabled = true;
}