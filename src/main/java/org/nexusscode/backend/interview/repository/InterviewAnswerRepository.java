package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {
}
