package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewStorageBoxRepository extends JpaRepository<InterviewSummaryStorageBox, Long> {
    List<InterviewSummaryStorageBox> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<InterviewSummaryStorageBox> findByIdAndUserId(Long storageBoxId, Long userId);

    @Query("""
    SELECT b FROM InterviewSummaryStorageBox b
    WHERE b.user.id = :userId
      AND (
            LOWER(b.sessionTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(b.strengths) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(b.weaknesses) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(b.overallAssessment) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    ORDER BY b.createdAt DESC
""")
    List<InterviewSummaryStorageBox> findAllByUserIdAndKeyword(@Param("userId") Long userId,
                                                               @Param("keyword") String keyword);

}
