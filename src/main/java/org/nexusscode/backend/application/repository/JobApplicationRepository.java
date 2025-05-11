package org.nexusscode.backend.application.repository;

import java.util.List;
import org.nexusscode.backend.application.domain.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByCompanyNameContainingIgnoreCaseOrJobTitleContainingIgnoreCase(String searchWord, String searchWord1,
        Pageable pageable);
}
