package org.nexusscode.backend.resume.repository;

import java.util.List;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findAllByApplication(JobApplication application);
}
