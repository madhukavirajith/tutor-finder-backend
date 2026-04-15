package com.madhuka.tutor_finder_backend.repository; // Adjust package name to yours

import com.madhuka.tutor_finder_backend.entity.User; // Adjust package name to yours
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // This method automatically searches for a user by email
    Optional<User> findByEmail(String email);

    // This checks if an email already exists
    Boolean existsByEmail(String email);
}