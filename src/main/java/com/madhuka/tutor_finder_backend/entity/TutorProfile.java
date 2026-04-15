package com.madhuka.tutor_finder_backend.entity; // Adjust package name to yours

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "tutor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-One relationship: One User can only have One Tutor Profile
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String fullName;
    private String phoneNumber;
    private String location;
    @Column(length = 1000)
    private String bio;
    private String profileImageUrl;

    // "PENDING", "APPROVED", "REJECTED"
    private String approvalStatus = "PENDING";

    // Many-to-Many: A Tutor can teach many Subjects
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tutor_subjects",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;
}