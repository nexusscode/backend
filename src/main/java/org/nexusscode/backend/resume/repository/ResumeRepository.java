package org.nexusscode.backend.resume.repository;

import org.nexusscode.backend.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
