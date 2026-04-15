package com.madhuka.tutor_finder_backend.dto; // Adjust package name to yours

import lombok.Data;
@Data
public class LoginRequest {
    private String email;
    private String password;
}