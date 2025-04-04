package org.nexusscode.backend.resume.repository;

import org.nexusscode.backend.resume.domain.ResumeItemFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeItemFeedbackRepository extends JpaRepository<ResumeItemFeedback, Long> {
}
