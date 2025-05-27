package org.nexusscode.backend.application.repository;

import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("""
    SELECT a FROM JobApplication a
    WHERE a.user = :user AND (
          LOWER(a.companyName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR
          LOWER(a.jobTitle) LIKE LOWER(CONCAT('%', :searchWord, '%'))
    )
    """)
    Page<JobApplication> searchByUserAndCompanyOrTitle(@Param("user") User user,
        @Param("searchWord") String searchWord,
        Pageable pageable);

    Page<JobApplication> findAllByUser(User user, Pageable pageable);
}
