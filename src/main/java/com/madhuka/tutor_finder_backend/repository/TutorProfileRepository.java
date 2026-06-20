package com.madhuka.tutor_finder_backend.repository; // Adjust package name to yours

import com.madhuka.tutor_finder_backend.entity.TutorProfile; // Adjust package name to yours
import com.madhuka.tutor_finder_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    // Find tutors by approval status (for Admin page)
    List<TutorProfile> findByApprovalStatus(String status);
    
    long countByApprovalStatus(String status);

    Optional<TutorProfile> findByUser(User user);

    // SUPER IMPORTANT: Search by Name OR Subject
    @Query("SELECT DISTINCT t FROM TutorProfile t JOIN t.subjects s " +
            "WHERE t.approvalStatus = 'APPROVED' AND " +
            "(LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TutorProfile> searchApprovedTutors(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT t FROM TutorProfile t LEFT JOIN t.subjects s " +
            "WHERE t.approvalStatus = 'APPROVED' AND " +
            "(:search IS NULL OR :search = '' OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:location IS NULL OR :location = '' OR LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:subject IS NULL OR :subject = '' OR LOWER(s.name) = LOWER(:subject))")
    Page<TutorProfile> searchApprovedTutorsWithFilters(
            @Param("search") String search,
            @Param("location") String location,
            @Param("subject") String subject,
            Pageable pageable);

    Page<TutorProfile> findByApprovalStatus(String status, Pageable pageable);
}