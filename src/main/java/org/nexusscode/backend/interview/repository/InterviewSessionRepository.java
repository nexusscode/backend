package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
}
