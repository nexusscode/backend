package org.nexusscode.backend.application.repository;

import org.nexusscode.backend.application.domain.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

}
