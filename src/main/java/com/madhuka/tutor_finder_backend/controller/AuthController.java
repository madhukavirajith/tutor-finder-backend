package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.dto.*;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.entity.User;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import com.madhuka.tutor_finder_backend.repository.UserRepository;
import com.madhuka.tutor_finder_backend.security.JwtUtils;
import com.madhuka.tutor_finder_backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TutorProfileRepository tutorProfileRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user account with personal details
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole().toUpperCase());
        user.setTitle(signUpRequest.getTitle());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setGender(signUpRequest.getGender());
        user.setDateOfBirth(signUpRequest.getDateOfBirth());

        userRepository.save(user);

        // If registering as a TUTOR, auto-create their TutorProfile with contact info
        if ("TUTOR".equalsIgnoreCase(signUpRequest.getRole())) {
            TutorProfile profile = new TutorProfile();
            profile.setUser(user);
            profile.setApprovalStatus("PENDING");

            // Combine title + firstName + lastName as fullName
            String fullName = buildFullName(signUpRequest.getTitle(), signUpRequest.getFirstName(), signUpRequest.getLastName());
            profile.setFullName(fullName);
            profile.setPhoneNumber(signUpRequest.getPhoneNumber());
            profile.setLocation(signUpRequest.getAddress());

            tutorProfileRepository.save(profile);
        }

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Combine title + first + last name into a single display name.
     * e.g. "Dr. Amara Perera"
     */
    private String buildFullName(String title, String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (title != null && !title.isBlank()) {
            sb.append(title).append(". ");
        }
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName).append(" ");
        }
        if (lastName != null && !lastName.isBlank()) {
            sb.append(lastName);
        }
        return sb.toString().trim();
    }
}