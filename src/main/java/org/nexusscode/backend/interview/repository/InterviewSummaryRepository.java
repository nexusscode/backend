package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {
}
