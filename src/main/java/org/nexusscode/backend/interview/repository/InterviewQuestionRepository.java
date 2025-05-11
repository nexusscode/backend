package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    @EntityGraph(attributePaths = "answer")
    @Query("SELECT q FROM InterviewQuestion q WHERE q.session.id = :sessionId AND q.seq = :seq")
    Optional<InterviewQuestion> findBySessionIdAndSeq(@Param("sessionId") Long sessionId, @Param("seq") int seq);

    @Query("SELECT q FROM InterviewQuestion q WHERE q.session.id = :sessionId order by q.seq")
    Optional<List<InterviewQuestion>> findBySessionId(@Param("sessionId") Long sessionId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE InterviewQuestion q SET q.TTSFileName = :ttsUrl WHERE q.id = :id")
    void updateTTSUrlById(@Param("id") Long id, @Param("ttsUrl") String ttsUrl);


    @EntityGraph(attributePaths = {"answer", "session"})
    Optional<InterviewQuestion> findById(Long questionId);
}
