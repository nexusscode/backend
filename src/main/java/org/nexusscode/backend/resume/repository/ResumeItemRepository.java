package org.nexusscode.backend.resume.repository;

import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResumeItemRepository extends JpaRepository<ResumeItem, Long> {
    @Query("select ri from ResumeItem ri where ri.resume.id = :id order by ri.id")
    List<ResumeItem> findByResumeId(Long id);
}
