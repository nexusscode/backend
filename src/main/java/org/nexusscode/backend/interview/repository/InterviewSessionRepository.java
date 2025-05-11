package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewQnADTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    @Query("""
            SELECT new org.nexusscode.backend.interview.dto.InterviewSessionDetailDto(
                s.id,
                s.title,
                q.id,
                q.questionText,
                a.id,
                a.transcript
            )
            FROM InterviewSession s
            JOIN s.questions q
            LEFT JOIN InterviewAnswer a ON a.question = q
            WHERE s.id = :sessionId
            ORDER BY q.seq
    """)
    Optional<List<InterviewSessionDetailDto>> findInterviewSessionDetail(@Param("sessionId") Long sessionId);

    @Query("""
    SELECT new org.nexusscode.backend.interview.dto.InterviewAdviceDTO(
        q.id,
        q.questionText,
        a.id,
        a.transcript,
        f.id,
        f.feedbackText
    )
    FROM InterviewSession s
    JOIN s.questions q
    LEFT JOIN InterviewAnswer a ON a.question = q
    LEFT JOIN AnswerFeedback f ON f.answer = a
    WHERE s.id = :sessionId
    ORDER BY q.seq
""")
    Optional<List<InterviewAdviceDTO>> findInterviewAdviceBySessionId(@Param("sessionId") Long sessionId);

    @Query("""
    SELECT new org.nexusscode.backend.interview.dto.InterviewQnADTO(
        q.id, q.questionText, a.transcript, f.feedbackText, a.audioLength 
    )
    FROM InterviewSession s
    JOIN s.questions q
    LEFT JOIN InterviewAnswer a ON a.question = q
    LEFT JOIN AnswerFeedback f ON f.answer = a
    WHERE s.id = :sessionId
    ORDER BY q.seq
""")
    Optional<List<InterviewQnADTO>> findInterviewQnABySessionId(@Param("sessionId") Long sessionId);

    @Query("""
        SELECT new org.nexusscode.backend.interview.dto.InterviewSessionDTO(
            s.id, s.title
        )
        FROM InterviewSession s
        WHERE s.application.id = :applicationId
        ORDER BY s.createdAt DESC
    """)
    Optional<List<InterviewSessionDTO>> findSessionListByApplicationId(@Param("applicationId") Long applicationId);

    @EntityGraph(attributePaths = {"questions", "questions.answer"})
    Optional<InterviewSession> findById(Long sessionId);
}
