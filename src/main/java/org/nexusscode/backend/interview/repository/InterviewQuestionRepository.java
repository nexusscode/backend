package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
}
