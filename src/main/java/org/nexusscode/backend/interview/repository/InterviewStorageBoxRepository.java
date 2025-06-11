package org.nexusscode.backend.interview.repository;

import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewStorageBoxRepository extends JpaRepository<InterviewSummaryStorageBox, Long> {
    List<InterviewSummaryStorageBox> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<InterviewSummaryStorageBox> findByIdAndUserId(Long storageBoxId, Long userId);
}
