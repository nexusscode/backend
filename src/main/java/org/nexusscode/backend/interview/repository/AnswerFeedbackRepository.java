package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.AnswerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerFeedbackRepository extends JpaRepository<AnswerFeedback, Long> {

    public Optional<AnswerFeedback> findByAnswerId(Long answerId);
}
