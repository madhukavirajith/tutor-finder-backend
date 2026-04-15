package com.madhuka.tutor_finder_backend.dto; // Adjust package name to yours

import lombok.Data;
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String role;

    public JwtResponse(String accessToken, Long id, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}