package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {

    @Query("""
    SELECT i.summary
    FROM InterviewSummary i
    WHERE i.session.id = :sessionId
""")
    Optional<String> findSummaryBySessionId(@Param("sessionId") Long sessionId);

}
