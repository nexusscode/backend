package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.AnswerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerFeedbackRepository extends JpaRepository<AnswerFeedback, Long> {
}
