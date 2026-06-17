package com.madhuka.tutor_finder_backend.dto;

import lombok.Data;

@Data
public class SignupRequest {
    // Core credentials (required for all roles)
    private String email;
    private String password;
    private String role; // "TUTOR" or "PARENT"

    // Personal information (required for TUTOR, optional for PARENT)
    private String title;       // Mr, Mrs, Miss, Dr, Prof
    private String firstName;
    private String lastName;
    private String gender;      // Male, Female, Other
    private String dateOfBirth; // ISO format: YYYY-MM-DD
    private String phoneNumber;
    private String address;     // Maps to TutorProfile.location
}