package com.madhuka.tutor_finder_backend.repository;

import com.madhuka.tutor_finder_backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Set<Subject> findByNameIn(List<String> names);
}