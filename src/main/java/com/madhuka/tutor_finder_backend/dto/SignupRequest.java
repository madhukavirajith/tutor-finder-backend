package com.madhuka.tutor_finder_backend.dto; // Adjust package name to yours

import lombok.Data;
@Data
public class SignupRequest {
    private String email;
    private String password;
    private String role; // "TUTOR" or "PARENT"
}