package org.nexusscode.backend.resume.repository;

import org.nexusscode.backend.resume.domain.ResumeFeedback;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeFeedbackRepository extends JpaRepository<ResumeFeedback, Long> {

    ResumeFeedback findTopByResumeItemOrderByCreatedAtDesc(ResumeItem resumeItem);
}
