package com.madhuka.tutor_finder_backend.repository; // Adjust package name to yours

import com.madhuka.tutor_finder_backend.entity.TutorProfile; // Adjust package name to yours
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    // Find tutors by approval status (for Admin page)
    List<TutorProfile> findByApprovalStatus(String status);

    // SUPER IMPORTANT: Search by Name OR Subject
    @Query("SELECT DISTINCT t FROM TutorProfile t JOIN t.subjects s " +
            "WHERE t.approvalStatus = 'APPROVED' AND " +
            "(LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TutorProfile> searchApprovedTutors(@Param("keyword") String keyword, Pageable pageable);
    Page<TutorProfile> findByApprovalStatus(String status, Pageable pageable);
}