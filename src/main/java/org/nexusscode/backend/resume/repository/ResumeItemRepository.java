package org.nexusscode.backend.resume.repository;

import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeItemRepository extends JpaRepository<ResumeItem, Long> {
}
