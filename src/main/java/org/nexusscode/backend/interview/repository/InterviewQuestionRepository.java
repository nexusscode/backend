package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    @Query("SELECT q FROM InterviewQuestion q WHERE q.session.id = :sessionId AND q.seq = :seq")
    Optional<InterviewQuestion> findBySessionIdAndSeq(@Param("sessionId") Long sessionId, @Param("seq") int seq);

}
